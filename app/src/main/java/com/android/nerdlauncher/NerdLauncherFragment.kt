package com.android.nerdlauncher


import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.util.*


@Suppress("NAME_SHADOWING")
class NerdLauncherFragment : Fragment() {

    private var mRecyclerView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false)

        mRecyclerView = v.findViewById(R.id.app_recycler_view)
        mRecyclerView!!.layoutManager = LinearLayoutManager(activity)

        setupAdapter()
        return v
    }

    // Connects RecyclerView.Adapter instance to RecyclerView object
    private fun setupAdapter() {
        val startupIntent = Intent(Intent.ACTION_MAIN)
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pm = activity!!.packageManager
        val activities = pm.queryIntentActivities(startupIntent, 0).also {

            it.sortWith(Comparator { a, b ->
                val pm = activity!!.packageManager
                String.CASE_INSENSITIVE_ORDER.compare(
                    a.loadLabel(pm).toString(),
                    b.loadLabel(pm).toString()
                )
            })
        }

        mRecyclerView!!.adapter = ActivityAdapter(activities)
    }

    private inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var mResolveInfo: ResolveInfo? = null
        private val mImageView: ImageView = itemView.findViewById<View>(R.id.app_icon) as ImageView
        private val mNameTextView: TextView = itemView.findViewById<View>(R.id.app_title) as TextView

        init {
            mImageView.setOnClickListener(this)
            mNameTextView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            mResolveInfo = resolveInfo
            val pm = activity!!.packageManager
            val appName = mResolveInfo!!.loadLabel(pm).toString()
            val imageIcon = mResolveInfo!!.loadIcon(pm)
            mImageView.setImageDrawable(imageIcon)
            mNameTextView.text = appName
        }

        override fun onClick(v: View) {
            val activityInfo = mResolveInfo!!.activityInfo

            val i = Intent(Intent.ACTION_MAIN)
                .setClassName(activityInfo.applicationInfo.packageName, activityInfo.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(i)
        }
    }

    private inner class ActivityAdapter(private val mActivities: List<ResolveInfo>) :
        RecyclerView.Adapter<ActivityHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(activity)
            val view = layoutInflater.inflate(R.layout.app_list_item, parent, false)

            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = mActivities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return mActivities.size
        }
    }

    companion object {

        private val TAG = "NerdLauncherFragment"

        fun newInstance(): NerdLauncherFragment {

            val args = Bundle()

            val fragment = NerdLauncherFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
