package net.minepact.api.discord

import net.minepact.Main
import net.minepact.api.discord.embed.Embed
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

class Webhook(
    var webhookUrl: String? = Main.MAIN_CONFIG.webhookUrl,
    val username: String,
    val avatarUrl: URL?,
) {
    fun sendMessage(content: String? = null, embeds: List<Embed> = emptyList()) {
        val json = formatAsJSON(content, embeds)
        if (webhookUrl.isNullOrBlank()) {
            Main.instance.logger.warning("Webhook URL is not configured; skipping sendMessage().")
            return
        }

        val client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl!!))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(10))
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build()

        try {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() != 204) {
                Main.instance.logger.warning($$"Failed to send webhook: ${response.statusCode()} ${response.body()}")
            }
        } catch (e: Exception) {
            Main.instance.logger.severe($$"Failed to send webhook: ${e.message}")
        }
    }

    private fun formatAsJSON(content: String?, embeds: List<Embed>): String {
        val builder: StringBuilder = StringBuilder()
        builder.append("{")
        builder.append("\"username\": \"").append(escapeJson(username)).append("\",")
        if (avatarUrl != null) {
            builder.append("\"avatar_url\": \"").append(escapeJson(avatarUrl.toString())).append("\",")
        }
        if (content != null) {
            builder.append("\"content\": \"").append(escapeJson(content)).append("\",")
        }
        if (embeds.isNotEmpty()) {
            builder.append("\"embeds\": [")
            embeds.forEach { embed ->
                builder.append(embed.toJSON())
                if (embed != embeds.last()) {
                    builder.append(",")
                }
            }
            builder.append("]")
        }
        if (builder.last() == ',') {
            builder.setLength(builder.length - 1)
        }
        builder.append("}")
        return builder.toString()
    }

    private fun escapeJson(input: String): String {
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}