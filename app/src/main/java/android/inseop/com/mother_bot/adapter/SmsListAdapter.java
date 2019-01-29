package android.inseop.com.mother_bot.adapter;

import android.content.Context;
import android.inseop.com.mother_bot.R;
import android.inseop.com.mother_bot.item_data.SmsItemData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 인섭 on 2019-01-23.
 */

public class SmsListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<SmsItemData> m_oData = null;
    private int nListCnt = 0;

    public SmsListAdapter(ArrayList<SmsItemData> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return m_oData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView oTextTitle = (TextView) convertView.findViewById(R.id.textTitle);
        //EditText oTextDate = (EditText) convertView.findViewById(R.id.textPhone);
        if(m_oData.size() != 0) {
            oTextTitle.setText(m_oData.get(position).getStrMessage());
        }
        //oTextDate.setText(m_oData.get(position).getStrDate());
        return convertView;
    }

}
