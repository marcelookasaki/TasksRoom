package com.example.tasksapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasksapp.R
import com.example.tasksapp.data.model.Status
import com.example.tasksapp.databinding.FragmentTasksBinding
import com.example.tasksapp.util.showBottomSheet
import com.myo.tasksapp.data.model.Task
import com.myo.tasksapp.ui.adapter.TaskAdapter
import com.myo.tasksapp.ui.adapter.TaskTopAdapter


@Suppress("UnusedEquals", "UnusedEquals", "UnusedEquals", "UnusedEquals", "UnusedEquals",
    "UnusedEquals"
)
class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskTopAdapter: TaskTopAdapter

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initRecyclerView()
        observerViewModel()
        viewModel.getTasks()
    }

    private fun initListeners() {
        binding.fabTf.setOnClickListener {
            val action = TasksFragmentDirections
                .actionTasksFragmentToFormTaskFragment(null)
            findNavController().navigate(action)
        }
        observerViewModel()
    }

    private fun observerViewModel() {
        // List
        viewModel.taskList.observe(viewLifecycleOwner) { stateView ->
            when(stateView){
                is StateView.OnLoad -> {
                    binding.todoFragmentPB.isVisible = true
                }

                is StateView.OnSuccess -> {
                    val taskList = stateView.data?.filter { it.status == Status.TODO }
                    binding.todoFragmentPB.isVisible = false
                    listEmpty(taskList ?: emptyList())
                    taskAdapter.submitList(taskList)
                }

                is StateView.OnError -> {
                    Toast.makeText(
                        requireContext(),
                        stateView.message,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.todoFragmentPB.isVisible = false
                }
            }
        }

        viewModel.taskInsert.observe(viewLifecycleOwner) { stateView ->

            when(stateView){
                is StateView.OnLoad -> {
                    binding.todoFragmentPB.isVisible = true
                }

                is StateView.OnSuccess -> {
                    binding.todoFragmentPB.isVisible = false

                    if (stateView.data?.status == Status.TODO) {

                        // Armazena a lista atual do adapter
                        val oldList = taskAdapter.currentList

                        // Gera uma nova lista a partir da lista antiga já com a tarefa atualizada
                        val newList = oldList.toMutableList().apply {
                            add(0, stateView.data)
                        }

                        // Envia a lista atualizada para o adapter
                        taskAdapter.submitList(newList)

                        setPositionRecyclerView()
                    }
                }

                is StateView.OnError -> {
                    Toast.makeText(
                        requireContext(),
                        stateView.message,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.todoFragmentPB.isVisible = false
                }
            }
        }

        viewModel.taskUpdate.observe(viewLifecycleOwner) { stateView ->

            when(stateView){
                is StateView.OnLoad -> {
                    binding.todoFragmentPB.isVisible = true
                }

                is StateView.OnSuccess -> {
                    binding.todoFragmentPB.isVisible = false

                    // Armazena a lista atual do adapter
                    val oldList = taskAdapter.currentList

                    // Gera uma nova lista a partir da lista antiga já com a tarefa atualizada
                    val newList = oldList.toMutableList().apply {
                        //Se lista antiga contém a tarefa sendo editada e status for todo
                        //Adiciona a tarefa na posição 0
                        if (!oldList.contains(stateView.data)&&
                            stateView.data?.status == Status.TODO) {
                            add(0, stateView.data)
                            setPositionRecyclerView()
                        }
                        // Filtro se status for to do verificar descrição senão remove
                        if (stateView.data?.status == Status.TODO) {
                            find { it.id == stateView.data.id }?.description == stateView.data.description
                        }else {
                            remove(stateView.data)
                        }
                    }

                    // Armazena a posição da tarefa a ser atualizada na lista
                    val position = newList.indexOfFirst { it.id == stateView.data?.id }

                    // Envia a lista atualizada para o adapter
                    taskAdapter.submitList(newList)

                    // Atualiza a tarefa pela posição do adapter
                    taskAdapter.notifyItemChanged(position)

                    listEmpty(newList)
                }

                is StateView.OnError -> {
                    Toast.makeText(
                        requireContext(),
                        stateView.message,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.todoFragmentPB.isVisible = false
                }
            }
        }

        viewModel.taskDelete.observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.OnLoad -> {
                    binding.todoFragmentPB.isVisible = true
                }

                is StateView.OnSuccess -> {
                    binding.todoFragmentPB.isVisible = false

                    Toast.makeText(
                        requireContext(),
                        R.string.task_delete_success,
                        Toast.LENGTH_LONG
                    ).show()

                    // Armazena a lista atual do adapter
                    val oldList = taskAdapter.currentList

                    // Gera uma nova lista a partir da lista antiga já com a tarefa atualizada
                    val newList = oldList.toMutableList().apply {
                        remove(stateView.data)
                    }
                    taskAdapter.submitList(newList)

                    listEmpty(newList)
                }

                is StateView.OnError -> {
                    Toast.makeText(
                        requireContext(),
                        stateView.message,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.todoFragmentPB.isVisible = false
                }
            }
        }
    }

    private fun setPositionRecyclerView() {
        taskAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {

            }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {

            }
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {

            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.rvTasksTodo.scrollToPosition(0)
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {

            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {

            }
        })
    }

    private fun initRecyclerView() {
        taskTopAdapter = TaskTopAdapter { task, option ->
            optionSelected(task, option)
        }
        taskAdapter = TaskAdapter { task, option ->
            optionSelected(task, option)
        }

        val concatAdapter = ConcatAdapter(taskTopAdapter, taskAdapter)

        with(binding.rvTasksTodo) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = concatAdapter
        }
    }

    private fun optionSelected(task: Task, option: Int) {
        when(option) {
            TaskAdapter.SELECT_REMOVE -> {
                showBottomSheet(
                    titleDialog = R.string.title_dialog_delete,
                    message = getString(R.string.message_dialog_delete),
                    titleButton = R.string.text_btn_dialog_confirm,
                    onClick = {
                        viewModel.deleteTask(task)
                    }
                )
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = TasksFragmentDirections
                    .actionTasksFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_DETAILS -> {
                Toast.makeText(
                    requireContext(),
                    "Detalhes de ${task.description}",
                    Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun listEmpty(taskList: List<Task>) {
        binding.tvTodoFragmentTaskList.text = if (taskList.isEmpty()) {
            getString(R.string.task_list_empty)
        }else {
            ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}