package io.github.horaciocome1.reaque.ui.posts.read

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import io.github.horaciocome1.reaque.data.bookmarks.BookmarksRepository
import io.github.horaciocome1.reaque.data.posts.Post
import io.github.horaciocome1.reaque.data.posts.PostsRepository
import io.github.horaciocome1.reaque.data.ratings.RatingsRepository
import io.github.horaciocome1.reaque.data.readings.ReadingsRepository
import io.github.horaciocome1.reaque.data.shares.SharesRepository
import io.github.horaciocome1.reaque.data.users.User
import io.github.horaciocome1.reaque.util.buildShortDynamicLink

class ReadPostViewModel(
    private val postsRepository: PostsRepository,
    private val readingsRepository: ReadingsRepository,
    private val sharesRepository: SharesRepository,
    private val ratingsRepository: RatingsRepository,
    private val bookmarksRepository: BookmarksRepository
) : ViewModel() {

    private val dynamicLinks: FirebaseDynamicLinks by lazy {
        FirebaseDynamicLinks.getInstance()
    }

    var readingPost = Post("")
        set(value) {
            if (value.id.isNotBlank() && value.id != field.id)
                readingsRepository.read(value) {}
            field = value
        }

    var posts: List<Post> = mutableListOf()

    var myRating = 0

    val navigateUp: (View) -> Unit = {
        it.findNavController()
            .navigateUp()
    }

    fun get(post: Post) = postsRepository.get(post)

    fun getRating(post: Post) = ratingsRepository.get(post)

    fun setRating(view: View, post: Post, value: Int) {
        ratingsRepository.set(post, value) { }
        navigateUp(view)
    }

    fun share(fragment: ReadPostFragment, view: View, post: Post) {
        view.isEnabled = false
        dynamicLinks.buildShortDynamicLink(post) {
            if (fragment.isVisible) {
                fragment.startActivity(it)
                sharesRepository.share(post) {
                    view.isEnabled = true
                }
            }
        }
    }

    fun bookmark(view: View, post: Post) {
        view.isEnabled = false
        bookmarksRepository.bookmark(post) {
            view.visibility = View.GONE
        }
    }

    fun unBookmark(view: View, post: Post) {
        view.isEnabled = false
        bookmarksRepository.unBookmark(post) {
            view.visibility = View.GONE
        }
    }

    fun isBookmarked(post: Post) = bookmarksRepository.isBookmarked(post)

    fun openUserProfile(view: View, user: User) {
        val directions = ReadPostFragmentDirections
            .actionOpenUserProfileFromReadPost(user.id)
        view.findNavController()
            .navigate(directions)
    }

    fun openSetRating(view: View, post: Post) {
        val directions = ReadPostFragmentDirections
            .actionOpenSetRating(post.id, myRating)
        view.findNavController()
            .navigate(directions)
    }

}