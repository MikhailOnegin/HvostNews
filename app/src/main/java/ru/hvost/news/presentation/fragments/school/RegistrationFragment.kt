package ru.hvost.news.presentation.fragments.school

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.hvost.news.App
import ru.hvost.news.MainViewModel
import ru.hvost.news.R
import ru.hvost.news.data.api.response.PetsResponse
import ru.hvost.news.databinding.FragmentRegistrationBinding
import ru.hvost.news.models.CitiesOffline
import ru.hvost.news.models.Pets
import ru.hvost.news.presentation.adapters.spinners.SpinnerAdapter
import ru.hvost.news.presentation.dialogs.AddPetRegistrationDialog
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.events.DefaultNetworkEventObserver
import ru.hvost.news.utils.getValue

class RegistrationFragment: BaseFragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var schoolVM: SchoolViewModel
    private lateinit var mainVM: MainViewModel
    private lateinit var loadPetsEvent:DefaultNetworkEventObserver
    private var idSchool:String? = null
    private var pets: List<Pets>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        mainVM = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mainVM.loadPetsData()
        setListeners()
        setObservers(this)
        binding.spinnerPets.setSelection(0, false)
        idSchool = arguments?.getString("schoolId")
        val text = resources.getString(R.string.accept_terms_of_agreement)
        val spannable = SpannableString(text)
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
        val clickableSpan1 = object :ClickableSpan(){
            override fun onClick(p0: View) {
                val newIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://yandex.ru/legal/rules")
                )
                startActivity(newIntent)
            }
        }
        spannable.setSpan(clickableSpan1,19, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(colorSpan, 19, 46, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.checkBox2.text = spannable
        binding.checkBox2.movementMethod = LinkMovementMethod.getInstance()


    }

    @Suppress("UNCHECKED_CAST")
    private fun initializedEvents() {
        loadPetsEvent = DefaultNetworkEventObserver(
            anchorView = binding.root,
            doOnSuccess = {
            }
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun setObservers(owner:LifecycleOwner) {
        schoolVM.onlineSchools.observe(owner, {
            idSchool?.run {
                for (i in it.onlineSchools.indices) {
                    val school = it.onlineSchools[i]
                    if (school.id.toString() == this) {
                        val head = "Регистрация на онлайн семинар \"${school.title}\""
                        binding.textViewHead.text = head
                    }
                }
            }
        })
        mainVM.userPets.observe(owner, {
            if (it.isNotEmpty()) {
                binding.spinnerPets.isEnabled = true
                pets = it
                binding.spinnerPets.adapter =
                    SpinnerAdapter(requireContext(), "", arrayListOf(), Pets::petName)
                val adapter = (binding.spinnerPets.adapter as SpinnerAdapter<Pets>)
                adapter.clear()
                (binding.spinnerPets.adapter as SpinnerAdapter<Pets>).addAll(it)
            }

        })
        schoolVM.enabledRegister.observe(owner, {
            binding.buttonCompleteRegistration.isEnabled = it
        })
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Suppress("UNCHECKED_CAST")
    private fun setListeners() {
        binding.toolbarRegistration.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.imageButtonAddPet.setOnClickListener {
            AddPetRegistrationDialog().show(
                childFragmentManager,
                "info_dialog"
            )
        }
        binding.buttonCompleteRegistration.setOnClickListener {
            if(binding.checkBox2.isChecked && binding.spinnerPets.size > 0 ){
                var petId:String? = null
                    (binding.spinnerPets.adapter as SpinnerAdapter<Pets>).getItem(0)?.let {pets2 ->
                        pets?.let {
                            for(i in it.indices){
                                val pet = it[i]
                                if( pet == pets2){
                                    petId = pet.petId
                                }
                            }
                        }
                }
                App.getInstance().userToken?.let {userToken ->
                    idSchool?.let { idSchool ->

                        petId?.let {idPet ->
                            schoolVM.setParticipate(userToken, idSchool, idPet)
                        }

                    }

                }
                findNavController().popBackStack()
            }
        }
        binding.checkBox2.setOnCheckedChangeListener { _, b ->
            schoolVM.enabledRegister.value = b && binding.spinnerPets.adapter is SpinnerAdapter<*> && binding.spinnerPets.size > 0
        }
        binding.spinnerPets.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            @Suppress("UNCHECKED_CAST")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                schoolVM.enabledRegister.value = binding.checkBox2.isChecked && binding.spinnerPets.adapter is SpinnerAdapter<*> && binding.spinnerPets.size > 0
            }
        }
    }
}