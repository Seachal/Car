package com.pengyu.car.provider

import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.pengyu.car.CarEntity
import com.pengyu.car.CarEntity.Goods

/**
 *
 */
class NodeSectionAdapter : BaseNodeAdapter() {
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


}