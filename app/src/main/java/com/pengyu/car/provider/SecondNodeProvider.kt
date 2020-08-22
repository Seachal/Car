package com.pengyu.car.provider

import android.view.View
import android.widget.Toast
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.pengyu.car.CarEntity.Goods
import com.pengyu.car.R

class SecondNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1

    override val layoutId: Int
        get() = R.layout.item_car_goods

    override fun convert(helper: BaseViewHolder, data: BaseNode) {
        if (data == null) {
            return
        }
        val entity = data as Goods
        //        helper.seti(R.id.goods_img, entity.getUrls());
        helper.setText(R.id.goods_name, entity.goodsname)
    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        Toast.makeText(context, "点击了内容区域$position", Toast.LENGTH_SHORT).show()
    }
}