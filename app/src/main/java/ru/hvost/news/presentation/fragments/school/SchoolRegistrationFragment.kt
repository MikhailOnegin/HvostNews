package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.databinding.FragmentSchoolRegistrationBinding
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.models.Pets
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.dialogs.AddPetCustomDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.getValue
import ru.hvost.news.utils.startIntentActionView

class SchoolRegistrationFragment: BaseFragment() {

    private lateinit var binding: FragmentSchoolRegistrationBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var mainVM: MainViewModel
    private lateinit var loadPetsEvent:DefaultNetworkEventObserver
    private lateinit var setParticipateEvent:DefaultNetworkEventObserver
    private var onlineSchool:OnlineSchools.OnlineSchool? = null
    private var idSchool:String? = null
    private var pets: List<Pets>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainVM.loadPetsData()
        initializedEvents()
        setListeners()
        setObservers(this)

        binding.spinnerPets.setSelection(0, false)
        idSchool = arguments?.getString("schoolId")
        val text = resources.getString(R.string.accept_terms_of_agreement)
        val spannable = SpannableString(text)
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                startIntentActionView(requireContext(), "https://hvost.news/upload/iblock/78f/Politika-konfidentsialnosti-hvost.news.pdf")
            }
        }
        spannable.setSpan(clickableSpan1, 19, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(colorSpan, 19, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.checkBox2.text = spannable
        binding.checkBox2.movementMethod = LinkMovementMethod.getInstance()
        binding.spinnerPets.adapter = SpinnerAdapter(requireContext(), "", arrayListOf("Нет питомцев"), String::getValue)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializedEvents() {
        loadPetsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
                mainVM.userPets.value?.run {
                    if (this.isNotEmpty()) {
                        binding.spinnerEmptyView.root.visibility = View.GONE
                        binding.spinnerPets.visibility = View.VISIBLE
                    }
                    else {
                        binding.spinnerEmptyView.root.visibility = View.VISIBLE
                        binding.spinnerPets.visibility = View.GONE
                    }
                    if (this.isNotEmpty()) {
                        binding.spinnerPets.isEnabled = true
                        pets = this
                        binding.spinnerPets.adapter =
                            SpinnerAdapter(requireContext(), "", arrayListOf(), Pets::petName)
                        val adapter = (binding.spinnerPets.adapter as SpinnerAdapter<Pets>)
                        adapter.clear()
                        (binding.spinnerPets.adapter as SpinnerAdapter<Pets>).addAll(this)
                    }
                }
            }
        )
        setParticipateEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnLoading = { binding.buttonCompleteRegistration.isEnabled = false },
            doOnSuccess = {
                binding.buttonCompleteRegistration.isEnabled = false
                onlineSchool?.run {
                    schoolVM.successRegistration.value = true
                    val bundle = Bundle()
                    bundle.putString("schoolTitle", title)
                    bundle.putString("schoolId", id.toString())
                    findNavController().navigate(
                        R.id.action_registrationSchoolFragment_to_schoolFragment,
                        bundle
                    )
                }
            },
            doOnError = { binding.buttonCompleteRegistration.isEnabled = true },
            doOnFailure = { binding.buttonCompleteRegistration.isEnabled = true }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun setObservers(owner: LifecycleOwner) {
        mainVM.userPetsLoadingEvent.observe(owner, loadPetsEvent)
        schoolVM.enabledSchoolRegister.observe(owner, {
            binding.buttonCompleteRegistration.isEnabled = it
        })
        schoolVM.setParticipateEvent.observe(owner, setParticipateEvent)
        schoolVM.onlineSchools.observe(owner, {
            idSchool?.run {
                for (i in it.onlineSchools.indices) {
                    val school = it.onlineSchools[i]
                    if (school.id.toString() == this) {
                        onlineSchool = school
                        val head = "${getString(R.string.registrated_online_school)} \"${school.title}\""
                        binding.textViewHead.text = head
                    }
                }
            }
        })
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Suppress("UNCHECKED_CAST")
    private fun setListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageButtonAddPet.setOnClickListener {
            AddPetCustomDialog().show(
                    childFragmentManager,
                    "info_dialog"
            )
        }
        binding.buttonCompleteRegistration.setOnClickListener {
            var petId: String? = null
            (binding.spinnerPets.adapter as SpinnerAdapter<Pets>).getItem(0)?.let { pets2 ->
                pets?.let {
                    for (i in it.indices) {
                        val pet = it[i]
                        if (pet == pets2) {
                            petId = pet.petId
                        }
                    }
                }
            }
            App.getInstance().userToken?.let { userToken ->
                idSchool?.let { idSchool ->
                    petId?.let { idPet ->
                        schoolVM.setParticipate(userToken, idSchool, idPet)
                    }
                }
            }
        }
        binding.checkBox2.setOnCheckedChangeListener { _, b ->
            schoolVM.enabledSchoolRegister.value =
                b && binding.spinnerPets.adapter is SpinnerAdapter<*> && binding.spinnerPets.size > 0
        }
        binding.spinnerPets.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            @Suppress("UNCHECKED_CAST")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                schoolVM.enabledSchoolRegister.value =
                    binding.checkBox2.isChecked && binding.spinnerPets.adapter is SpinnerAdapter<*> && binding.spinnerPets.size > 0
            }
        }
    }
}