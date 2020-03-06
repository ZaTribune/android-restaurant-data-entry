package shadow.android.data_entry_1.client;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import shadow.android.data_entry_1.db.Day;
import shadow.android.data_entry_1.R;
import shadow.android.data_entry_1.db.DBController;
import shadow.android.data_entry_1.ui.DisplayHelper;
import shadow.android.data_entry_1.ui.Offset;

import static shadow.android.data_entry_1.client.ClientFragment.BREAKFAST;
import static shadow.android.data_entry_1.client.ClientFragment.DINNER;
import static shadow.android.data_entry_1.client.ClientFragment.LUNCH;

public class DayAdapter extends BaseAdapter {
    private List<Day> days;
    private LayoutInflater inflater;
    private Context context;
    private DayFocusChangeListener listener;


    public DayAdapter(Context context,List<Day> days) {
    this.context=context;
    inflater=LayoutInflater.from(context);
    this.days=days;
    listener=new DayFocusChangeListener();
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    @Override
    public int getCount() {
        return days.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        TextView tv_day;
        EditText et_breakfast;
        EditText et_lunch;
        EditText et_dinner;
        ImageButton btn_breakfast;
        ImageButton btn_lunch;
        ImageButton btn_dinner;
        int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.day,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.tv_day=convertView.findViewById(R.id.tv_day);
            viewHolder.et_breakfast=convertView.findViewById(R.id.et_breakfast);
            viewHolder.et_lunch=convertView.findViewById(R.id.et_lunch);
            viewHolder.et_dinner=convertView.findViewById(R.id.et_dinner);

            viewHolder.btn_breakfast=convertView.findViewById(R.id.btn_breakfast);
            viewHolder.btn_lunch=convertView.findViewById(R.id.btn_lunch);
            viewHolder.btn_dinner=convertView.findViewById(R.id.btn_dinner);

            viewHolder.et_breakfast.setTag(BREAKFAST);
            viewHolder.et_lunch.setTag(LUNCH);
            viewHolder.et_dinner.setTag(DINNER);

            viewHolder.btn_breakfast.setTag(BREAKFAST);
            viewHolder.btn_lunch.setTag(LUNCH);
            viewHolder.btn_dinner.setTag(DINNER);

            days.get(position).setIndex(position);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
            //position=viewHolder.position;
        }

        DateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        //int ratio=position*86400000;
        //viewHolder.tv_day.setText(dateFormat.format(new Date(from.getTimeInMillis()+ratio)));
        viewHolder.tv_day.setTag(days.get(position));//later is used
        viewHolder.et_breakfast.setText(String.valueOf(days.get(position).getBreakfast()));
        viewHolder.et_lunch.setText(String.valueOf(days.get(position).getLunch()));
        viewHolder.et_dinner.setText(String.valueOf(days.get(position).getDinner()));
        viewHolder.tv_day.setText(dateFormat.format(days.get(position).getDay()));


        viewHolder.et_breakfast.setOnFocusChangeListener(new DayFocusChangeListener());
        viewHolder.et_lunch.setOnFocusChangeListener(new DayFocusChangeListener());
        viewHolder.et_dinner.setOnFocusChangeListener(new DayFocusChangeListener());

        viewHolder.btn_breakfast.setOnClickListener(infoClickListener);
        viewHolder.btn_lunch.setOnClickListener(infoClickListener);
        viewHolder.btn_dinner.setOnClickListener(infoClickListener);
        //viewHolder.position=position;

        return convertView;
    }
    View.OnClickListener infoClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText et_info;
            Button btn_submit_info;
            Day day= (Day)((View)view.getParent()).findViewById(R.id.tv_day).getTag();//to get the tagged day
            PopupWindow popupWindow=new PopupWindow(view.getContext());
            popupWindow.setContentView(LayoutInflater.from(view.getContext()).inflate(R.layout.info,null));
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(null);
            Offset offset= DisplayHelper.centerOnParent((AppCompatActivity) context,popupWindow.getContentView());
            popupWindow.showAsDropDown(view.getRootView(),offset.getX(),offset.getY());
            et_info=popupWindow.getContentView().findViewById(R.id.et_info);
            btn_submit_info=popupWindow.getContentView().findViewById(R.id.btn_submit_info);
            btn_submit_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("xxxxxxx",""+et_info.getText().toString());
                    long id=DBController.updateDayInfo(v.getContext(),day.getId(),view.getTag().toString(),et_info.getText().toString(),day);
                    popupWindow.dismiss();
                }
            });
            dimPopupParent(true,popupWindow);
            switch (view.getTag().toString()){
                case BREAKFAST:
                    et_info.setText(day.getBreakfastInfo());
                    break;
                case LUNCH:
                    et_info.setText(day.getLunchInfo());
                    break;
                case DINNER:
                    et_info.setText(day.getDinnerInfo());
                    break;
            }
        }
    };

    class DayFocusChangeListener implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText editText= (EditText) v;
            Day day= (Day)((View)editText.getParent()).findViewById(R.id.tv_day).getTag();
            Log.i(DayFocusChangeListener.class.getSimpleName()+" "+((EditText) v).getText(),"hasFocus "+hasFocus);
             if(!hasFocus) {
                 double value;
                 if(String.valueOf(editText.getText()).equals(""))
                     return;

                     value=Double.valueOf(String.valueOf(editText.getText()));
                 Log.i("inputValue",""+value);
                 long id = DBController.updateDay(context, day.getId(), editText.getTag().toString(),value);

                 switch (editText.getTag().toString()){
                     case BREAKFAST:
                         day.setBreakfast(value);
                         break;
                     case LUNCH:
                         day.setLunch(value);
                         break;
                     case DINNER:
                         day.setDinner(value);
                         break;
                 }
             }
        }
    }
    private void dimPopupParent(boolean b,PopupWindow window) {
        WindowManager wm = (WindowManager) window.getContentView().getContext().getSystemService(Context.WINDOW_SERVICE);
        View popupParent= (View) window.getContentView().getParent();
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) popupParent.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        if(b)
            p.dimAmount = 0.4f;
        else p.dimAmount=1f;
        if (wm != null) {
            wm.updateViewLayout(popupParent, p);
        }
    }
}
