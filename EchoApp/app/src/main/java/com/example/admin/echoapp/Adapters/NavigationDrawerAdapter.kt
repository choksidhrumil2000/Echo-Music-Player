package com.example.admin.echoapp.Adapters

import android.content.Context
import android.location.SettingInjectorService
import android.support.constraint.R.id.parent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.admin.echoapp.Activities.MainActivity
import com.example.admin.echoapp.Fragements.AboutusFragment
import com.example.admin.echoapp.Fragements.FavouriteFragement
import com.example.admin.echoapp.Fragements.MainScreenFragement
import com.example.admin.echoapp.Fragements.SettingsFragemnt
import com.example.admin.echoapp.R
import org.w3c.dom.Text

class NavigationDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray, _context: Context) :
    RecyclerView.Adapter<NavigationDrawerAdapter.NavViewholder>() {


    var _contentList: ArrayList<String>? = null
    var getimages: IntArray? = null
    var mContext: Context? = null

    init {
        this._contentList = _contentList
        this.getimages = _getImages
        this.mContext = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): NavigationDrawerAdapter.NavViewholder {

        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_nav_drawer, parent, false)
        var Returnthis = NavViewholder(itemView)
        return Returnthis
    }

    override fun getItemCount(): Int {

        return (_contentList as ArrayList).size
    }

    override fun onBindViewHolder(holder: NavigationDrawerAdapter.NavViewholder, position: Int) {

        holder.icon_GET?.setBackgroundResource(getimages?.get(position) as Int)
        holder.text_GET?.setText(_contentList?.get(position))
        holder.contentHolder?.setOnClickListener {

            if (position == 0) {
                val mainScreenFragement = MainScreenFragement()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragement, mainScreenFragement)
                    .commit()
            } else if (position == 1) {
                val favouriteFragement = FavouriteFragement()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragement, favouriteFragement)
                    .commit()
            } else if (position == 2) {
                val settingsFragement = SettingsFragemnt()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragement, settingsFragement)
                    .commit()
            } else {
                val aboutusFragement = AboutusFragment()
                (mContext as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragement, aboutusFragement)
                    .commit()
            }

            MainActivity.Statified.drawerlayout?.closeDrawers()
        }
    }

    class NavViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var icon_GET: ImageView? = null
        var text_GET: TextView? = null
        var contentHolder: RelativeLayout? = null


        init {
            icon_GET = itemView.findViewById(R.id.icon_navdrawer)
            text_GET = itemView.findViewById(R.id.text_navdrawer)
            contentHolder = itemView.findViewById(R.id.nav_drawer_content_holder)

        }
    }

}