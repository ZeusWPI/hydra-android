package be.ugent.zeus.hydra.news

import android.os.Parcelable
import be.ugent.zeus.hydra.common.ArticleViewer
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

/**
 * A news article. The fields' meaning are the same as for Atom, from which
 * they come.
 * 
 * @author Niko Strijbol
 */
@JsonClass(generateAdapter = true)
@Parcelize
data class NewsArticle(
    val id: String,
    internal val link: String,
    internal val title: String,
    val content: String?,
    val published: OffsetDateTime?,
    val summary: String?,
    var updated: OffsetDateTime?
): Parcelable, ArticleViewer.Article {
    override fun getLink(): String = link
    override fun getTitle(): String = title
}