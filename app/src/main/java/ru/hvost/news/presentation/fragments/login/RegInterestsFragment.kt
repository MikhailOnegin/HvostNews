package ru.hvost.news.presentation.fragments.login

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.hvost.news.App
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentRegInterestsBinding
import ru.hvost.news.models.RegInterest
import ru.hvost.news.presentation.adapters.recycler.RegInterestsAdapter
import ru.hvost.news.utils.createSnackbar
import ru.hvost.news.utils.enums.State
import ru.hvost.news.utils.events.NetworkEvent

class RegInterestsFragment : Fragment() {

    private lateinit var binding: FragmentRegInterestsBinding
    private lateinit var registrationVM: RegistrationVM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegInterestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registrationVM = ViewModelProvider(requireActivity())[RegistrationVM::class.java]
        setRecyclerView()
        setObservers()
    }

    override fun onStart() {
        super.onStart()
        setListeners()
        registrationVM.setStage(RegistrationVM.RegStep.INTERESTS)
    }

    private fun setListeners() {
        binding.buttonFinish.setOnClickListener(onButtonFinishClicked)
    }

    private fun setObservers() {
        registrationVM.interests.observe(viewLifecycleOwner) { onInterestsChanged(it) }
        registrationVM.thirdStageFinished.observe(viewLifecycleOwner) { onThirdStageFinished(it) }
        registrationVM.registrationState.observe(viewLifecycleOwner) { onRegistrationStateChanged(it) }
    }

    private val onRegistrationStateChanged = { event: NetworkEvent<State> ->
        when(event.getContentIfNotHandled()) {
            State.SUCCESS -> {
                createSnackbar(
                    binding.root,
                    getString(R.string.registrationSuccessfull),
                    getString(R.string.buttonOk)
                ).show()
                requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
            }
            State.ERROR -> {
                createSnackbar(
                    binding.root,
                    event.error,
                    getString(R.string.buttonOk)
                ).show()
                binding.buttonFinish.isEnabled = true
            }
            State.FAILURE -> {
                createSnackbar(
                    binding.root,
                    getString(R.string.networkFailureMessage),
                    getString(R.string.buttonOk)
                ).show()
                binding.buttonFinish.isEnabled = true
            }
            State.LOADING -> {
                binding.buttonFinish.isEnabled = false
            }
            null -> {}
        }
    }

    private fun onThirdStageFinished(thirdStageFinished: Boolean?) {
        binding.buttonFinish.isEnabled = thirdStageFinished == true
    }

    private fun onInterestsChanged(interests: List<RegInterest>?) {
        interests?.run {
            (binding.recyclerView.adapter as RegInterestsAdapter).submitList(this)
        }
    }

    private val onButtonFinishClicked: (View)->Unit = { _: View ->
        registrationVM.registerUser()
    }

    private fun setRecyclerView() {
        val buttonHeight = resources.getDimension(R.dimen.buttonHeight)
        val margin = resources.getDimension(R.dimen.normalMargin)
        val padding = buttonHeight + margin
        binding.recyclerView.apply {
            updatePadding(top=margin.toInt(), bottom = padding.toInt())
            addItemDecoration(RvItemDecorations())
            adapter = RegInterestsAdapter(registrationVM)
        }
    }

    class RvItemDecorations : RecyclerView.ItemDecoration() {

        private val elementsMargin = App.getInstance().resources.getDimension(R.dimen.smallMargin).toInt()
        private val sideMargin = App.getInstance().resources.getDimension(R.dimen.largeMargin).toInt()

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if(position % 2 == 0){
                outRect.left = sideMargin
                outRect.right = elementsMargin
            }else{
                outRect.left = elementsMargin
                outRect.right = sideMargin
            }
            outRect.bottom = elementsMargin * 2
        }
    }

}