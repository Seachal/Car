package com.pengyu.car

import android.graphics.Paint
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.kyleduo.switchbutton.SwitchButton

/**
 * Created by PengYu on 2017/10/18.
 */

class CarAdapter(data: ArrayList<BaseNode>, private val selectChangeListener: SelectChangeListener) : BaseNodeAdapter(data) {

    private var isDEL = false                   //是否为删除状态

    //  监听器
    private var clickItem: ClickItem? = null    //保存被选中的Item

    /**
     * 定义Item类型
     */
    companion object {
        val TYPE_LEVEL_0 = 0
        val TYPE_LEVEL_1 = 1
    }

    init {
        addItemType(TYPE_LEVEL_0, R.layout.item_car_sj)     //商家布局ID
        addItemType(TYPE_LEVEL_1, R.layout.item_car_goods)  //商品布局ID
    }

    override fun convert(helper: BaseViewHolder, item: BaseNode) {
        when (helper.itemViewType) {
            TYPE_LEVEL_0 -> {   //商家Item
                val carEntity = item as CarEntity
                helper.setText(R.id.sj_name, carEntity.name)
//               取消了 setchecked 方法 
                (helper.getView<AppCompatCheckBox>(R.id.check_sj)).setChecked(carEntity.isChecked)
                val check_sj = helper.getView<CheckBox>(R.id.check_sj)
                if (!isDEL) {
                    check_sj.isChecked = carEntity.isChecked
                } else {
                    check_sj.isChecked = carEntity.isDelChecked
                }
                checkSJITem(carEntity, check_sj)
                helper.itemView.setOnClickListener {
                    if (clickItem != null) {
                        clickItem!!.clickSJItem(helper.adapterPosition, carEntity)
                    }
                }
                val switch_button = helper.getView<SwitchButton>(R.id.switch_button)
                switch_button.setOnCheckedChangeListener { buttonView, isChecked ->
                    buttonView.setOnClickListener {
                        carEntity.isBjchecked = isChecked
                        for (subItem in carEntity.childNode!!) {
                            (subItem as CarEntity.Goods).isChecked = isChecked
                        }
                        notifyDataSetChanged()
                    }
                }
                switch_button.isChecked = carEntity.isBjchecked
            }
            TYPE_LEVEL_1 -> {   //商品Item
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
                if (!isDEL) {
                    check_goods.isChecked = goods.isChecked
                } else {
                    check_goods.isChecked = goods.isDelChecked
                }
                checkGoodsItem(goods, check_goods)
                helper.itemView.setOnClickListener {
                    if (clickItem != null) {
                        clickItem!!.clickGoodsItem(helper.adapterPosition, goods)
                    }
                }
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
                        selectChangeListener.goodsChangeS(getCheckItem(), isCheckAllSJs())
                    }

                })
            }
        }
    }

    /**
     * 全选 反全选
     *
     * @param b 选择状态
     */
    fun CheckAll(b: Boolean) {
        for (multiItemEntity in data) {
            if (b) {
                if (multiItemEntity is CarEntity) {
                    //商家item状态
                    if (!isDEL) {
                        multiItemEntity.isChecked = true
                    } else {
                        multiItemEntity.isDelChecked = true
                    }
                } else {
                    //商品状态
                    if (!isDEL) {
                        (multiItemEntity as CarEntity.Goods).isChecked = true
                    } else {
                        (multiItemEntity as CarEntity.Goods).isDelChecked = true
                    }
                }
            } else {
                if (multiItemEntity is CarEntity) {
                    if (!isDEL) {
                        multiItemEntity.isChecked = false
                    } else {
                        multiItemEntity.isDelChecked = false
                    }
                } else {
                    if (!isDEL) {
                        (multiItemEntity as CarEntity.Goods).isChecked = false
                    } else {
                        (multiItemEntity as CarEntity.Goods).isDelChecked = false
                    }
                }
            }
        }
        notifyDataSetChanged()
        selectChangeListener.goodsChangeS(getCheckItem(), isCheckAllSJs())
    }

    /**
     * 点击商家的item给商家状态赋值，并给商家的商品赋值
     *
     * @param carEntity     单个商家
     * @param check_sj 商家checkbox
     */
    private fun checkSJITem(carEntity: CarEntity, check_sj: CheckBox) {
        check_sj.setOnCheckedChangeListener { compoundButton, isChecked ->
            compoundButton.setOnClickListener {
                if (!isDEL) {
                    carEntity.isChecked = isChecked
                } else {
                    carEntity.isDelChecked = isChecked
                }

                for (good in carEntity.childNode!!) {
                    if (!isDEL) {
                        (good as CarEntity.Goods).isChecked = isChecked
                    } else {
                        (good as CarEntity.Goods).isDelChecked = isChecked
                    }
                }
                notifyDataSetChanged()
                selectChangeListener.goodsChangeS(getCheckItem(), isCheckAllSJs())
            }
        }
    }

    /**
     * 点击商品的item给商品的状态赋值
     *
     * @param goods       单个商品
     * @param check_goods 商品checkbox
     */
    private fun checkGoodsItem(goods: CarEntity.Goods, check_goods: CheckBox) {
        check_goods.setOnCheckedChangeListener { compoundButton, isChecked ->

            compoundButton.setOnClickListener {
                if (!isDEL) {
                    goods.isChecked = isChecked
                } else {
                    goods.isDelChecked = isChecked
                }
                if (isChecked) {
                    isCheckAllGoods(goods.orgid)
                }
                if (!isChecked) {
                    isNOCheckAllGoods(goods.orgid)
                }
                notifyDataSetChanged()
                selectChangeListener.goodsChangeS(getCheckItem(), isCheckAllSJs())
            }
        }
    }

    /**
     * 判断是否全选商品 ，（sca: 如果商品全选中了，商家也会跟着选中）
     */
    private fun isCheckAllGoods(orgid: Long) {
        for (datum in data) {
            if (datum is CarEntity) {
//                找到商家 id
                if (orgid == datum.orgid) {
                    for (good in datum.childNode!!) {
                        if (!isDEL) {
                            if (!(good as CarEntity.Goods).isChecked) {
                                return
                            }
                        } else {
                            if (!(good as CarEntity.Goods).isDelChecked) {
                                return
                            }
                        }
                    }
                    if (!isDEL) {
                        datum.isChecked = true
                    } else {
                        datum.isDelChecked = true
                    }

                }
            }
        }
    }

    /**
     * 判断是否有一个未选商品
     */
    private fun isNOCheckAllGoods(orgid: Long) {
        for (datum in data) if (datum is CarEntity) {
            if (orgid == datum.orgid) {
                for (good in datum.childNode!!) {
                    if (!isDEL) {
                        if (!(good as CarEntity.Goods).isChecked) {
                            datum.isChecked = false
                            return
                        }
                    } else {
                        if (!(good as CarEntity.Goods).isDelChecked) {
                            datum.isDelChecked = false
                            return
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断是否全选商家
     */
    private fun isCheckAllSJs(): Boolean {
        if (data.size == 0) {
            return false
        }
        if (!isDEL) {
            return data.none { it is CarEntity && !it.isChecked }
        } else {
            return data.none { it is CarEntity && !it.isDelChecked }
        }
    }

    /**
     *返回所有选中的 Goods Item
     */
    private fun getCheckItem(): ArrayList<CarEntity.Goods> {
        var goods = ArrayList<CarEntity.Goods>()
        if (!isDEL) {
            data.filterIsInstance<CarEntity.Goods>().filterTo(goods) { it.isChecked }
        } else {
            data.filterIsInstance<CarEntity.Goods>().filterTo(goods) { it.isDelChecked }
        }
        return goods
    }

    /**
     *删除选中的Item
     */
    fun removeChecked() {
        data
                .filterIsInstance<CarEntity>()
                .forEach {
                    if (it.isDelChecked) {
                        data.remove(it)
                        for (subItem in it.childNode!!) {
                            data.remove(subItem)
                        }
                    } else {
                        it.childNode?.filter { (it as CarEntity.Goods).isDelChecked }?.forEach { data.remove(it) }
                    }
                }
        notifyDataSetChanged()
        selectChangeListener.goodsChangeS(getCheckItem(), isCheckAllSJs())
    }

    /**
     *  item自定义点击事件
     */
    interface ClickItem {
        fun clickSJItem(position: Int, carEntity: CarEntity)
        fun clickGoodsItem(position: Int, goods: CarEntity.Goods)
    }
    /**
     *  设置点击事件
     */
    fun setOnClickListener(clickItem: ClickItem) {
        this.clickItem = clickItem
    }


    /**
     * @del 赋值isDEL 并且更新itm状态 返回选中的Item
     */
    fun delete(del: Boolean) {
        isDEL = del
        notifyDataSetChanged()
        selectChangeListener.goodsChangeS(getCheckItem(), isCheckAllSJs())
    }
}
