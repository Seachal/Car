package com.pengyu.car.provider

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.chad.library.adapter.base.entity.node.BaseNode
import com.pengyu.car.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbarc.*

/**
 * Created by PengYu on 2017/10/18.
 */

class MainActivity2 : BaseActivity() {

    private var carAdapter: NodeSectionAdapter? = null
    private var allPrice: Float = 0.0f
    private var goods_xprice: TextView? = null
    private var checkItems: ArrayList<CarEntity.Goods> = ArrayList()
    private var delCheckItems: ArrayList<CarEntity.Goods> = ArrayList()
    private var createSJ: ArrayList<BaseNode>? = null
    private var context: Context = this
    private var isDel = false

    override fun setLayoutId(): Int {
        isImmersionBarEnabled = true
        return R.layout.activity_main
    }

    override fun initView() {
        goods_xprice = findViewById(R.id.goods_xprice) as TextView
        car_recy.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        createSJ = CreateData.createSJ()
        bar_title.text = resources.getString(R.string.gwc)

        //  selectChangeListener 中发生变化时，回调商品
        carAdapter = NodeSectionAdapter( object : SelectChangeListener {
            override fun goodsCheckChange(checkItem: ArrayList<CarEntity.Goods>, checkAllSJs: Boolean) {
                check_goods_all.isChecked = checkAllSJs
                //如果是删除商品状态这不需要计算操作
                if (isDel) {
//                  得到要删除的数据的列表
                    delCheckItems = checkItem
                    fkmd.setCenterString("删除(${delCheckItems!!.size})")
                    return
                }
                checkItems = checkItem
                allPrice = 0.0f
                for (goods in checkItem) {
                    allPrice += goods.goodsxprice.toFloat() * goods.goodscoun
                }
                fkmd.setCenterString("结算(${checkItem.size})")
                goods_xprice!!.setText("${allPrice}")
            }

        })

        carAdapter!!.setList(createSJ)
        car_recy.adapter = carAdapter



        //全选或反选监听
        check_goods_all.setOnCheckedChangeListener { compoundButton, isChecked ->
            compoundButton.setOnClickListener {
                carAdapter!!.CheckAll(isChecked)
            }
        }

        //删除或支付
        fkmd.setOnClickListener {
            if (isDel) {
                carAdapter!!.removeChecked()
            } else {
                Toast.makeText(context, "共支付：${goods_xprice!!.text}元", Toast.LENGTH_LONG).show()
            }
        }



        //管理商品事件监听 ,底部点击事件区域
        goods_gl.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isDel = true
                money_root.visibility = View.INVISIBLE
                fkmd.setCenterString("删除(${delCheckItems!!.size})")
                carAdapter!!.delete(isDel)
            } else {
                isDel = false
                money_root.visibility = View.VISIBLE
                fkmd.setCenterString("结算(${checkItems!!.size})")
                carAdapter!!.delete(isDel)
            }
        }
    }

    override fun initData() {
    }
}
