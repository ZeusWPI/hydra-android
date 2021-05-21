package be.ugent.zeus.hydra.news

import com.squareup.moshi.JsonClass
import java.time.OffsetDateTime

/**
 * A news stream, generated from an Atom stream.
 * 
 * @author Niko Strijbol
 */
@JsonClass(generateAdapter = true)
data class NewsStream(
    val generator: String,
    val id: String,
    val language: String,
    val link: String,
    val logo: String,
    val title: String,
    val updated: OffsetDateTime,
    val entries: List<NewsArticle>
)