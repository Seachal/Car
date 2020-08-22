package com.pengyu.car.provider

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.pengyu.car.CarEntity
import com.pengyu.car.CarEntity.Goods
import com.pengyu.car.MyNumberButton
import com.pengyu.car.R

class SecondNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = 1

    override val layoutId: Int
        get() = R.layout.item_car_goods

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        if (item == null) {
            return
        }
//        val entity = data as Goods
//        //        helper.seti(R.id.goods_img, entity.getUrls());
//        helper.setText(R.id.goods_name, entity.goodsname)


        val goods = item as CarEntity.Goods
        val goods_img = helper.getView<ImageView>(R.id.goods_img)
        var goods_count = helper.getView<TextView>(R.id.goods_count)
        goods_count.text = "x${goods.goodscoun}"
        Glide.with(context).load(goods.urls).into(goods_img)
        helper.setText(R.id.goods_name, goods.goodsname)
                .setText(R.id.goods_remake, goods.goodsremake)
                .setText(R.id.goods_xprice, goods.goodsxprice.toString() + "")
        val goods_yprice = helper.getView<TextView>(R.id.goods_yprice)
        goods_yprice.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG //中划线
        goods_yprice.text = "￥" + goods.goodsyprice
        val check_goods = helper.getView<CheckBox>(R.id.check_goods)
        if (!(getAdapter() as NodeSectionAdapter).isDEL) {
            check_goods.isChecked = goods.isChecked
        } else {
            check_goods.isChecked = goods.isDelChecked
        }

        (getAdapter() as NodeSectionAdapter).checkGoodsItem(goods, check_goods)
        val number_button = helper.getView<MyNumberButton>(R.id.number_button)
        if (goods.isBjchecked) {
            helper.getView<TextView>(R.id.goods_count).visibility = View.INVISIBLE
            number_button.visibility = View.VISIBLE
            goods_yprice.visibility = View.GONE
        } else {
            helper.getView<TextView>(R.id.goods_count).visibility = View.VISIBLE
            number_button.visibility = View.GONE
            goods_yprice.visibility = View.VISIBLE
        }
        number_button.setBuyMax(10)
                .setInventory(1000)
                .setCurrentNumber(goods.goodscoun)
        number_button.setOnChangeListener(object : MyNumberButton.ChangeListener {
            override fun change() {
                goods.goodscoun = number_button.number
                goods_count.text = "x${goods.goodscoun}"
                (getAdapter() as NodeSectionAdapter).selectChangeListener.goodsCheckChange( (getAdapter() as NodeSectionAdapter).getCheckItem(),  (getAdapter() as NodeSectionAdapter).isCheckAllSJs())
            }

        })

    }

    override fun onClick(helper: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        Toast.makeText(context, "点击了内容区域$position", Toast.LENGTH_SHORT).show()
    }
}