package ru.hvost.news.presentation.fragments.school

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.text.parseAsHtml
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_school_online_info.view.*
import kotlinx.android.synthetic.main.item_useful_literature.view.*
import kotlinx.android.synthetic.main.layout_literature_item.view.*
import kotlinx.android.synthetic.main.layout_what_wait_school.view.*
import ru.hvost.news.R
import ru.hvost.news.data.api.APIService.Companion.baseUrl
import ru.hvost.news.databinding.*
import ru.hvost.news.models.OnlineSchools
import ru.hvost.news.presentation.fragments.BaseFragment
import ru.hvost.news.presentation.viewmodels.SchoolViewModel
import ru.hvost.news.utils.startIntentActionView

class SchoolInfoFragment : BaseFragment() {
    private lateinit var binding: FragmentSchoolInfoBinding
    private lateinit var schoolVM: SchoolViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        schoolVM = ViewModelProvider(requireActivity())[SchoolViewModel::class.java]
        setObservers(this)
    }

    private fun setObservers(owner: LifecycleOwner) {
        schoolVM.schoolOnlineId.observe(owner, { schoolId ->
            schoolVM.onlineSchools.value?.let {
                for (i in it.onlineSchools.indices) {
                    val school = it.onlineSchools[i]
                    if (school.id == schoolId) {
                        Glide.with(requireActivity()).load(baseUrl + school.image).placeholder(R.drawable.empty_image).centerCrop().into(binding.imageViewInfo)
                        binding.textViewDescriptionWait.movementMethod = LinkMovementMethod()
                        binding.textViewDescriptionWait.text = school.description.parseAsHtml()
                        addViewToGridLayout(school)
                        if (school.literature.isNotEmpty()) {
                            binding.includeLiterature.root.visibility = View.VISIBLE
                            val containerLiterature = binding.includeLiterature.root.linearLayout_literature
                            containerLiterature.removeAllViews()
                            for (j in school.literature.indices) {
                                val literature = school.literature[j]
                                val viewLiterature = LayoutLiteratureItemBinding.inflate(
                                        LayoutInflater.from(requireContext()),
                                        containerLiterature,
                                        false
                                ).root

                                viewLiterature.textView_title.text = school.literature[j].title
                                viewLiterature.textView_pet.text = school.literature[j].pet
                                viewLiterature.constraint_literature.setOnClickListener {
                                    startIntentActionView(requireContext(), baseUrl + literature.fileUrl)
                                }
                                val paddingNormal = resources.getDimension(R.dimen.normalMargin).toInt()
                                val paddingEdge = resources.getDimension(R.dimen.largeMargin).toInt()

                                if (j == 0 || j == school.literature.lastIndex) {
                                    if (j == 0) viewLiterature.setPadding(paddingEdge, 0, paddingNormal, 0)
                                    else if (j == school.literature.lastIndex) viewLiterature.setPadding(0, 0, paddingEdge, 0)
                                } else viewLiterature.setPadding(0, 0, paddingNormal, 0)
                                containerLiterature.addView(viewLiterature)
                            }
                        }
                        return@observe
                    }
                }
            }
        })
    }
    private fun addViewToGridLayout(school:OnlineSchools.OnlineSchool){
        if (school.wait.isNotEmpty()) {
            binding.constraintWhatWait.visibility = View.VISIBLE
            val gridLayoutWait = binding.gridLayout
            gridLayoutWait.removeAllViews()
            for (q in school.wait.indices) {
                val wait = school.wait[q]
                val viewWait = LayoutWhatWaitSchoolBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    gridLayoutWait,
                    false
                ).root
                val param = GridLayout.LayoutParams()
                param.columnSpec = GridLayout.spec(
                    GridLayout.UNDEFINED,
                    GridLayout.FILL,
                    1f
                )
                param.width = 0
                val margin = resources.getDimension(R.dimen.normalMargin).toInt() / 2
                viewWait.layoutParams = param
                viewWait.textView_head.text = wait.head.parseAsHtml()
                viewWait.textView_description.text = wait.description.parseAsHtml()
                Glide.with(requireContext()).load(baseUrl + wait.imageUrl)
                    .placeholder(R.drawable.empty_image).centerCrop()
                    .into(viewWait.imageView_what_wait)
                (viewWait.layoutParams as GridLayout.LayoutParams).setMargins(
                    margin,
                    margin,
                    margin,
                    margin
                )
                gridLayoutWait.addView(viewWait)
                // Костыль для исправления бага с view на всю ширину gridLayout при единственном елементе
                if(school.wait.size == 1){
                    val viewWait2 = LayoutWhatWaitSchoolBinding.inflate(
                        LayoutInflater.from(requireContext()),
                        gridLayoutWait,
                        false
                    ).root
                    val param2 = GridLayout.LayoutParams()
                    param2.columnSpec = GridLayout.spec(
                        GridLayout.UNDEFINED,
                        GridLayout.FILL,
                        1f
                    )
                    param2.width = 0
                    viewWait2.layoutParams = param2
                    (viewWait2.layoutParams as GridLayout.LayoutParams).setMargins(
                        margin,
                        margin,
                        margin,
                        margin
                    )
                    viewWait2.visibility = View.INVISIBLE
                    gridLayoutWait.addView(viewWait2)
                }
            }
        }
    }
}