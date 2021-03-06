package com.suda.yzune.wakeupschedule.course_add

import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.suda.yzune.wakeupschedule.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_select_week.*
import org.jetbrains.anko.support.v4.dip

class SelectWeekFragment : DialogFragment() {

    var position = -1
    private lateinit var viewModel: AddCourseViewModel
    private val liveData = MutableLiveData<ArrayList<Int>>()
    private val result = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        viewModel = ViewModelProviders.of(activity!!).get(AddCourseViewModel::class.java)
        liveData.observe(this, Observer {
            if (it?.size == viewModel.maxWeek) {
                tv_all.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.white))
                tv_all.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (it?.size != viewModel.maxWeek) {
                tv_all.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
                tv_all.background = null
            }
            val flag = viewModel.judgeType(it!!, viewModel.maxWeek)
            if (flag == 1) {
                tv_type1.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.white))
                tv_type1.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (flag != 1) {
                tv_type1.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
                tv_type1.background = null
            }
            if (flag == 2) {
                tv_type2.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.white))
                tv_type2.background = ContextCompat.getDrawable(context!!, R.drawable.select_textview_bg)
            }
            if (flag != 2) {
                tv_type2.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
                tv_type2.background = null
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_select_week, container, false)
    }

    override fun onResume() {
        super.onResume()
        liveData.value = viewModel.editList[position].weekList.value
        result.addAll(liveData.value!!)
        showWeeks()
        initEvent()
    }

    private fun showWeeks() {
        ll_week.removeAllViews()
        val context = ll_week.context
        val margin = dip(4)
        val textViewSize = dip(32)
        val llHeight = dip(40)
        for (i in 0 until Math.ceil(viewModel.maxWeek / 6.0).toInt()) {
            val linearLayout = LinearLayout(context)
            linearLayout.orientation = LinearLayout.HORIZONTAL
            ll_week.addView(linearLayout)
            val params = linearLayout.layoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = llHeight
            linearLayout.layoutParams = params

            for (j in 0..5) {
                val week = i * 6 + j + 1
                if (week > viewModel.maxWeek) {
                    break
                }
                val textView = TextView(context)
                val textParams = LinearLayout.LayoutParams(textViewSize, textViewSize)
                textParams.setMargins(margin, margin, margin, margin)
                textView.layoutParams = textParams
                textView.text = "$week"
                textView.gravity = Gravity.CENTER
                if (week in result) {
                    textView.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.white))
                    textView.background = ContextCompat.getDrawable(context, R.drawable.week_selected_bg)
                } else {
                    textView.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
                    textView.background = null
                }

                textView.setOnClickListener {
                    if (textView.background == null) {
                        result.add(week)
                        liveData.value = result
                        textView.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.white))
                        textView.background = ContextCompat.getDrawable(context, R.drawable.week_selected_bg)
                    } else {
                        result.remove(week)
                        liveData.value = result
                        textView.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.black))
                        textView.background = null
                    }
                }
                textView.setLines(1)
                linearLayout.addView(textView)
                //selections[week - 1] = textView
            }
        }
    }

    private fun initEvent() {
        tv_all.setOnClickListener {
            if (tv_all.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            } else {
                result.clear()
                showWeeks()
                liveData.value = result
            }
        }

        tv_type1.setOnClickListener {
            if (tv_type1.background == null) {
                result.clear()
                for (i in 1..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        tv_type2.setOnClickListener {
            if (tv_type2.background == null) {
                result.clear()
                for (i in 2..viewModel.maxWeek step 2) {
                    result.add(i)
                }
                showWeeks()
                liveData.value = result
            }
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

        btn_save.setOnClickListener {
            if (result.size == 0){
                Toasty.error(context!!.applicationContext, "请至少选择一周").show()
            }
            else{
                viewModel.editList[position].weekList.value = result
                dismiss()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(arg: Int) =
                SelectWeekFragment().apply {
                    arguments = Bundle().apply {
                        putInt("position", arg)
                    }
                }
    }
}
