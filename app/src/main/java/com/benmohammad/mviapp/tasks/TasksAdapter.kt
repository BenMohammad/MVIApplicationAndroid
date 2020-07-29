package com.benmohammad.mviapp.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.benmohammad.mviapp.R
import com.benmohammad.mviapp.data.Task
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class TasksAdapter(tasks: List<Task>): BaseAdapter() {

    private val taskClickSubject = PublishSubject.create<Task>()
    private val taskToggleSubject = PublishSubject.create<Task>()
    private lateinit var tasks: List<Task>

    val taskClickObservable: Observable<Task>
    get() = taskClickSubject

    val taskToggleObservable: Observable<Task>
    get() = taskToggleSubject

    init {
        setList(tasks)
    }

    fun replaceData(tasks: List<Task>) {
        setList(tasks)
        notifyDataSetChanged()
    }

    private fun setList(tasks: List<Task>) {
        this.tasks = tasks
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView: View = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)

        val task = getItem(position)
        rowView.findViewById<TextView>(R.id.title).text = task.titleForList
        val completeCB = rowView.findViewById<CheckBox>(R.id.complete)

        completeCB.setOnClickListener { taskToggleSubject.onNext(task) }
        rowView.setOnClickListener { taskClickSubject.onNext(task) }

        return rowView

    }

    override fun getItem(position: Int): Task = tasks[position]


    override fun getItemId(position: Int): Long = position.toLong()



    override fun getCount(): Int = tasks.size
}