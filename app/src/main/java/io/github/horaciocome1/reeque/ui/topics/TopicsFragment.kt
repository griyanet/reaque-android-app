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

package io.github.horaciocome1.reeque.ui.topics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.horaciocome1.reeque.R
import io.github.horaciocome1.reeque.ui.MainActivity
import io.github.horaciocome1.reeque.ui.fragmentManager
import io.github.horaciocome1.reeque.utilities.InjectorUtils
import kotlinx.android.synthetic.main.fragment_topics.*

fun FragmentManager.loadTopics() {
    this.beginTransaction().replace(R.id.activity_main_container, TopicsFragment()).commit()
    fragmentManager = this
}

class TopicsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).supportActionBar?.show()
        initList(fragment_topics_recyclerview)
    }

    private fun initList(recyclerView: RecyclerView) {
        val factory = InjectorUtils.provideTopicsViewModelFactory()
        val viewModel = ViewModelProviders.of(this, factory).get(TopicsViewModel::class.java)
        viewModel.getTopics().observe(this, Observer { recyclerView.apply {
            layoutManager = LinearLayoutManager(recyclerView.context)
            adapter = TopicsAdapter(recyclerView.context, it, fragmentManager)
        }})
    }

}