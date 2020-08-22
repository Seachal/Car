package com.pengyu.car.provider

import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.widget.AppCompatCheckBox
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kyleduo.switchbutton.SwitchButton
import com.pengyu.car.CarAdapter
import com.pengyu.car.CarEntity
import com.pengyu.car.R

class RootNodeProvider : BaseNodeProvider() {



    override val itemViewType: Int
        get() = 0

    override val layoutId: Int
        get() = R.layout.item_car_sj



    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        val entity = item as CarEntity?
        helper.setText(R.id.sj_name, entity!!.name)


        val carEntity = item as CarEntity
        helper.setText(R.id.sj_name, carEntity.name)
//               取消了 setchecked 方法
        (helper.getView<AppCompatCheckBox>(R.id.check_sj)).setChecked(carEntity.isChecked)
        val check_sj = helper.getView<CheckBox>(R.id.check_sj)
        if (!  (getAdapter() as NodeSectionAdapter).isDEL) {
            check_sj.isChecked = carEntity.isChecked
        } else {
            check_sj.isChecked = carEntity.isDelChecked
        }
        (getAdapter() as NodeSectionAdapter).checkSJITem(carEntity, check_sj)

    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
//             getAdapter().expandOrCollapse(position);
        Toast.makeText(context, "点击了头区域$position", Toast.LENGTH_SHORT).show()
    }
}