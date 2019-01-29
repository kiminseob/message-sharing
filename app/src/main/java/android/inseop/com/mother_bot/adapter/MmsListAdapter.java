package android.inseop.com.mother_bot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.inseop.com.mother_bot.R;
import android.inseop.com.mother_bot.item_data.MmsItemData;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 인섭 on 2019-01-23.
 */

public class MmsListAdapter extends BaseAdapter {
    LayoutInflater inflater = null;
    private ArrayList<MmsItemData> m_oData = null;
    private int nListCnt = 0;

    public MmsListAdapter(ArrayList<MmsItemData> _oData)
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
        ImageView imageV = (ImageView) convertView.findViewById(R.id.imageView);
        if(m_oData.size()!=0) {
            if (m_oData.get(position).getBody() == null) {
                imageV.setImageBitmap(m_oData.get(position).getBitmap());
                if(m_oData.get(position).getExist()==1){
                    oTextTitle.setText("다운로드 완료된 파일");
                    oTextTitle.setTextColor(Color.parseColor("#71D09A"));
                }else {
                    oTextTitle.setText("다운로드 안됨");
                    oTextTitle.setTextColor(Color.WHITE);
                }
            } else {
                oTextTitle.setText(m_oData.get(position).getBody());
            }
        }

        //oTextDate.setText("전화번호 : "+m_oData.get(position).getPhoneNumber());
        return convertView;
    }

}
