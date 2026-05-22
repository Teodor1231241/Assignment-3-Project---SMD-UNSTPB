package com.phishguard.app.utils

import android.content.Context
import com.phishguard.app.PhishGuardApp
import com.phishguard.app.data.ThreatEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.util.regex.Pattern

data class DetectionResult(
    val isSuspicious: Boolean,
    val riskLevel: String,
    val reason: String
)

class PhishDetector(private val context: Context) {

    // Improved regex to capture URLs while ignoring surrounding noise
    private val urlPattern = Pattern.compile(
        "https?://[\\w\\d\\-._~:/?#\\[\\]@!$&'()*+,;=%]+",
        Pattern.CASE_INSENSITIVE
    )

    suspend fun analyzeMessage(sender: String, body: String): DetectionResult = withContext(Dispatchers.IO) {
        android.util.Log.d("PhishDetector", "Analyzing message from $sender: $body")
        val urls = extractUrls(body)
        android.util.Log.d("PhishDetector", "Found urls: $urls")

        val dao = PhishGuardApp.database.threatDao()
        var mostSevereResult: DetectionResult? = null

        // Even if no URL is found, check for extremely suspicious keywords in the text
        val bodyLower = body.lowercase()
        if (urls.isEmpty()) {
            if (bodyLower.contains("gift card") && (bodyLower.contains("win") || bodyLower.contains("congratulations"))) {
                val rewardRisk = DetectionResult(true, "MEDIUM", "Reward scam pattern (no URL detected)")
                dao.insert(ThreatEntry(url = "TEXT_ONLY_SCAM", riskLevel = rewardRisk.riskLevel, reason = rewardRisk.reason))
                return@withContext rewardRisk
            }
        }

        for (url in urls) {
            val risk = evaluateUrl(url, sender, body)
            android.util.Log.d("PhishDetector", "URL $url evaluated as: ${risk.riskLevel}")
            if (risk.isSuspicious) {
                dao.insert(ThreatEntry(url = url, riskLevel = risk.riskLevel, reason = risk.reason))
                
                if (mostSevereResult == null || (risk.riskLevel == "HIGH" && mostSevereResult.riskLevel != "HIGH")) {
                    mostSevereResult = risk
                }
            }
        }
        
        val finalResult = mostSevereResult ?: DetectionResult(false, "LOW", "Looks safe")
        android.util.Log.d("PhishDetector", "Final result: ${finalResult.riskLevel}")
        return@withContext finalResult
    }

    private fun extractUrls(text: String): List<String> {
        val matcher = urlPattern.matcher(text)
        val urls = mutableListOf<String>()
        while (matcher.find()) {
            var url = matcher.group()
            // Clean trailing characters that are likely not part of the URL (e.g. from markdown or punctuation)
            while (url.isNotEmpty() && (url.endsWith(".") || url.endsWith(",") || url.endsWith(")") || url.endsWith("]") || url.endsWith("!"))) {
                url = url.substring(0, url.length - 1)
            }
            if (url.isNotEmpty()) urls.add(url)
        }
        return urls
    }

    private fun evaluateUrl(url: String, sender: String, body: String): DetectionResult {
        val uri = try { URI(url) } catch (e: Exception) { null }
        val host = uri?.host?.lowercase() ?: ""
        val path = uri?.path?.lowercase() ?: ""
        val bodyLower = body.lowercase()

        val isBankRelated = host.contains("bank") || path.contains("bank") || bodyLower.contains("bank")
        val isUrgent = bodyLower.contains("urgent") || bodyLower.contains("immediately") || 
                       bodyLower.contains("leaked") || bodyLower.contains("expire") || 
                       bodyLower.contains("tonight") || bodyLower.contains("hurry")
        val isVerification = host.contains("login") || host.contains("secure") || host.contains("verify") ||
                             path.contains("login") || path.contains("secure") || path.contains("verify") ||
                             bodyLower.contains("verify") || bodyLower.contains("validate")
        
        val isReward = bodyLower.contains("congratulations") || bodyLower.contains("win ") || 
                       bodyLower.contains("won") || bodyLower.contains("gift card") || 
                       bodyLower.contains("reward") || bodyLower.contains("voucher") || 
                       bodyLower.contains("claim") || host.contains("reward") || 
                       host.contains("win-") || host.contains("free")

        if (isBankRelated && isUrgent) {
            return DetectionResult(true, "HIGH", "Bank impersonation + urgent threat")
        }
        if (isVerification && isUrgent) {
            return DetectionResult(true, "HIGH", "Suspicious link + urgency detected")
        }
        if (isReward || bodyLower.contains("selected to win")) {
            return DetectionResult(true, "MEDIUM", "Marketing scam / Reward voucher phishing")
        }
        if (isVerification || host.contains("update") || bodyLower.contains("account update")) {
            return DetectionResult(true, "MEDIUM", "Suspicious URL keywords")
        }
        if (containsHomograph(host)) {
            return DetectionResult(true, "HIGH", "Homograph character swap threat")
        }

        return DetectionResult(false, "LOW", "No heuristics tripped")
    }

    private fun containsHomograph(domain: String): Boolean {
        return domain.contains("rn") || domain.contains("vv") || (domain.contains("0") && domain.contains(".com"))
    }
}