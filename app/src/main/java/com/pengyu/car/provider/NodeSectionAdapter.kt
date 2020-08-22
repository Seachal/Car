package com.pengyu.car.provider

import android.widget.CheckBox
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseItemProvider
import com.pengyu.car.CarAdapter
import com.pengyu.car.CarEntity
import com.pengyu.car.CarEntity.Goods
import com.pengyu.car.SelectChangeListener

/**
 *
 */
class NodeSectionAdapter( internal val selectChangeListener: SelectChangeListener) : BaseNodeAdapter() {
    internal var isDEL = false                   //是否为删除状态


    override fun getItemType(data: List<BaseNode>, position: Int): Int {
        val node = data[position]
        //       根据节点的数据类型， 指定 itemType
        if (node is CarEntity) {
            return 0
        } else if (node is Goods) {
            return 1
        }
        return -1
    }

    // sca: 所有 item 都会点击这些节点，可以再定制下吗？
    init {

        //        添加需要铺满的 node provider
        addFullSpanNodeProvider(RootNodeProvider())
        //         添加 node provider
        addNodeProvider(SecondNodeProvider())

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
        selectChangeListener.goodsCheckChange(getCheckItem(), isCheckAllSJs())
    }

    /**
     * 点击商家的item给商家状态赋值，并给商家的商品赋值
     *
     * @param carEntity     单个商家
     * @param check_sj 商家checkbox
     */
    public fun checkSJITem(carEntity: CarEntity, check_sj: CheckBox) {
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
                selectChangeListener.goodsCheckChange(getCheckItem(), isCheckAllSJs())
            }
        }
    }

    /**
     * 点击商品的item给商品的状态赋值
     *
     * @param goods       单个商品
     * @param check_goods 商品checkbox
     */
    public fun checkGoodsItem(goods: CarEntity.Goods, check_goods: CheckBox) {
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
                selectChangeListener.goodsCheckChange(getCheckItem(), isCheckAllSJs())
            }
        }
    }

    /**
     * 判断是否全选商品 ，（sca: 如果商品全选中了，商家也会跟着选中）
     */
    public fun isCheckAllGoods(orgid: Long) {
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
    public fun isNOCheckAllGoods(orgid: Long) {
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
    public fun isCheckAllSJs(): Boolean {
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
    public fun getCheckItem(): ArrayList<CarEntity.Goods> {
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
    public  fun removeChecked() {
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
        selectChangeListener.goodsCheckChange(getCheckItem(), isCheckAllSJs())
    }



    /**
     * @del 赋值isDEL 并且更新itm状态 返回选中的Item
     */
    fun delete(del: Boolean) {
        isDEL = del
        notifyDataSetChanged()
        selectChangeListener.goodsCheckChange(getCheckItem(), isCheckAllSJs())
    }
}