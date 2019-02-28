/*
 *    Copyright 2019 Horácio Flávio Comé Júnior
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and limitations under the License.
 */

package io.github.horaciocome1.reaque.ui.favorites

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.github.horaciocome1.reaque.R
import io.github.horaciocome1.reaque.databinding.FragmentFavoritesTopicsBottomsheetBinding
import io.github.horaciocome1.reaque.ui.MainActivity
import io.github.horaciocome1.simplerecyclerviewtouchlistener.addSimpleTouchListener
import io.github.horaciocome1.simplerecyclerviewtouchlistener.setOnClick
import kotlinx.android.synthetic.main.fragment_favorites.*

class FavoritesFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onStart() {
        super.onStart()
        topics()
        posts()
        users()
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            (activity as MainActivity).supportActionBar?.hide()
    }

    private fun topics() {
        viewModel.topics.observe(this, Observer {
            fragment_favorites_topics_recyclerview.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = TopicsAdapter(it)
                setOnClick { view, position ->
                    BottomSheetDialog(context).apply {
                        val binding = FragmentFavoritesTopicsBottomsheetBinding.inflate(layoutInflater)
                        setContentView(binding.root)
                        show()
                        viewModel.getTopics(it[position]).observe(this@FavoritesFragment, Observer { topic ->
                            binding.topic = topic
                            binding.commentsButton.setOnClickListener {
                                val openComments =
                                    FavoritesFragmentDirections.actionOpenCommentsFromFavorites(topic.id, topic.title)
                                Navigation.findNavController(view).navigate(openComments)
                            }
                            binding.moreButton.setOnClickListener {
                                val openRead = FavoritesFragmentDirections.actionOpenReadFromFavorites(topic.id)
                                Navigation.findNavController(view).navigate(openRead)
                            }
                            binding.writersButton.setOnClickListener {
                                val openUsers =
                                    FavoritesFragmentDirections.actionOpenUsersFromFavorites(topic.id, topic.title)
                                Navigation.findNavController(view).navigate(openUsers)
                            }
                        })
                    }

                }
                addSimpleTouchListener()
            }
        })
    }

    private fun posts() {
        viewModel.posts.observe(this, Observer {
            fragment_favorites_posts_recyclerview.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = PostsAdapter(it)
                setOnClick { view, position ->
                    val openRead = FavoritesFragmentDirections.actionOpenReadFromFavorites(it[position].id)
                    Navigation.findNavController(view).navigate(openRead)
                }
                addSimpleTouchListener()
            }
        })
    }

    private fun users() {
        viewModel.users.observe(this, Observer {
            fragment_favorites_users_recyclerview.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = UsersAdapter(it)
                setOnClick { view, position ->
                    val openProfile = FavoritesFragmentDirections.actionOpenProfileFromFavorites(it[position].id)
                    Navigation.findNavController(view).navigate(openProfile)
                }
                addSimpleTouchListener()
            }
        })
    }

}