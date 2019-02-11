/*
 *    Copyright 2018 Horácio Flávio Comé Júnior
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

import io.github.horaciocome1.reaque.data.topics.Topic
import io.github.horaciocome1.reaque.data.users.User

class PostsRepository private constructor(private val postsWebService: PostsWebService) {

    fun addPost(post: Post) = postsWebService.addPost(post)

    fun getPosts(topic: Topic) = postsWebService.getPosts(topic)

    fun getPosts(post: Post) = postsWebService.getPosts(post)

    fun getPosts(user: User) = postsWebService.getPosts(user)

    val favorites = postsWebService.getFavorites()

    companion object {
        @Volatile
        private var instance: PostsRepository? = null

        fun getInstance(postsWebService: PostsWebService) = instance ?: synchronized(this) {
            instance ?: PostsRepository(postsWebService).also {
                instance = it
            }
        }
    }

}