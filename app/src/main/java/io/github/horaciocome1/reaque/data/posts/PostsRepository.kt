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

package io.github.horaciocome1.reaque.data.posts

import androidx.lifecycle.LiveData
import io.github.horaciocome1.reaque.data._posts.Post
import io.github.horaciocome1.reaque.data._topics.Topic
import io.github.horaciocome1.reaque.data._users.User

class PostsRepository private constructor(private val service: PostsWebService) {

    val favorites: LiveData<List<Post>> = service.favorites

    fun isThisFavoriteForMe(post: Post) = service.isThisMyFavorite(post)

    fun submitPost(post: Post, onSuccessful: () -> Unit) = service.submitPost(post, onSuccessful)

    fun getPosts(topic: Topic) = service.getPosts(topic)

    fun getPosts(post: Post) = service.getPosts(post)

    fun getPosts(user: User) = service.getPosts(user)


    companion object {

        @Volatile
        private var instance: PostsRepository? = null

        fun getInstance(service: PostsWebService) = instance ?: synchronized(this) {
            instance ?: PostsRepository(service).also {
                instance = it
            }
        }

    }

}