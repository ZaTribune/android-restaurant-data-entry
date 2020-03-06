package shadow.android.data_entry_1.client;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import shadow.android.data_entry_1.R;
import shadow.android.data_entry_1.db.Client;
import shadow.android.data_entry_1.db.DBController;
import shadow.android.data_entry_1.db.Day;
import shadow.android.data_entry_1.db.Period;
import shadow.android.data_entry_1.ui.DisplayHelper;
import shadow.android.data_entry_1.ui.Offset;
import static shadow.android.data_entry_1.ui.DisplayHelper.dimPopupParent;


public class ClientFragment extends Fragment {
    public static final String BREAKFAST="الإفطار";
    public static final String LUNCH="الغداء";
    public static final String DINNER="العشاء";
    public static final String TAG = ClientFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CLIENT = "client";

    // TODO: Rename and change types of parameters
    private Client client;
    private long period;
    private DatePicker dp_from;
    private DatePicker dp_to;
    private TextView  tv_from,tv_to,tv_number;
    private ListView lv_days;
    private ViewSwitcher vs_toolbar;
    private RelativeLayout btn_options;
    private PopupWindow popupWindow;
    private DayAdapter dayAdapter;
    private List<Day> days;
    private DateFormat dateFormat;
    private Activity activity;
    private int maxPeriodDays=20;
    private int minPeriodDays=10;

    private long getDisplayedPeriod() {
        return period;
    }

    private void setDisplayedPeriod(long period) {
        this.period = period;
    }

    public interface TaskIsDoneInterface{
        void done();
        void recreateFragment();
    }
    private TaskIsDoneInterface taskIsDoneInterface;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        taskIsDoneInterface= (TaskIsDoneInterface) getContext();
        activity= (Activity) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        taskIsDoneInterface.done();
        taskIsDoneInterface=null;
    }

    public ClientFragment() {
        // Required empty public constructor
    }


    public static ClientFragment newInstance(Client client) {
        ClientFragment fragment = new ClientFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CLIENT, client);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            client = (Client) getArguments().getSerializable(ARG_CLIENT);

        }
    }
    private View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(getContext(), btn_options);//2nd arg is the anchor
            menu.inflate(R.menu.options_client);
            menu.setOnMenuItemClickListener(menuItemClickListener);
            menu.show();
        }
    };
    private PopupMenu.OnMenuItemClickListener menuItemClickListener=new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Offset offset;
            popupWindow =new PopupWindow(getContext());
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(null);
            switch (item.getOrder()){//to cash in
                case 0:
                    if(dayAdapter==null){
                        Toast.makeText(getContext(),R.string.no_operations_available,Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    lv_days.requestFocus();//because data entry here is built on the focus change of the edit text
                    // so if an EditText is focused , its new value will be missed when calculating the sum
                    List<Day>list=DBController.getDays(getContext(),client.getId(),getDisplayedPeriod());
                    double sum=0;
                    for (Day day:list){
                        sum=sum+day.getBreakfast()+day.getLunch()+day.getDinner();
                    }
                    popupWindow.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.cashing,null));
                    offset=DisplayHelper.centerOnParent(activity, popupWindow.getContentView());
                    popupWindow.showAsDropDown(vs_toolbar,offset.getX(),offset.getY());
                    dimPopupParent(popupWindow.getContentView().getRootView(),true);
                    popupWindow.getContentView().findViewById(R.id.btn_cash).setOnClickListener(cashClickListener);
                    ((TextView)popupWindow.getContentView().findViewById(R.id.tv_total)).setText(getString(R.string.total,sum));
                    break;
                case 1:
                    if(lv_days.getCount()>0){
                        Toast.makeText(getContext(), R.string.clear_first, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    popupWindow.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.date_picker_0,null));
                    offset= DisplayHelper.centerOnParent(activity, popupWindow.getContentView());
                    popupWindow.showAsDropDown(vs_toolbar,offset.getX(),offset.getY());
                    dimPopupParent(popupWindow.getContentView().getRootView(),true);
                    dp_from= popupWindow.getContentView().findViewById(R.id.dp_from);
                    dp_to= popupWindow.getContentView().findViewById(R.id.dp_to);
                    popupWindow.getContentView().findViewById(R.id.btn_submit_user).setOnClickListener(submitPeriodClickListener);
                    break;
                    default:
                        Log.i("iiiiiiii","clicked");
            }
            return true;
        }
    };

    private View.OnClickListener submitPeriodClickListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Calendar from=Calendar.getInstance();
            Calendar to=Calendar.getInstance();
            from.set(dp_from.getYear(),dp_from.getMonth(),dp_from.getDayOfMonth());
            to.set(dp_to.getYear(),dp_to.getMonth(),dp_to.getDayOfMonth());
            long difference=((to.getTimeInMillis()-from.getTimeInMillis())/86400000)+1;//convert from ms to a day and add the last day
            //of the period
            Log.i("A NEW PERIOD ADDED","diff="+difference);
            if(to.before(from)) {
                Toast.makeText(getContext(), getString(R.string.error_period_beginning), Toast.LENGTH_SHORT).show();
                return;
            }
            if(difference>maxPeriodDays){
                Toast.makeText(getContext(), getString(R.string.period_max,maxPeriodDays), Toast.LENGTH_SHORT).show();
                return;
            }
            if(difference<minPeriodDays){
                Toast.makeText(getContext(), getString(R.string.period_min,minPeriodDays), Toast.LENGTH_SHORT).show();
                return;
            }

            dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tv_from.setText(getString(R.string.period_from,dateFormat.format(from.getTime())));
            tv_to.setText(getString(R.string.period_to,dateFormat.format(to.getTime())));
            //Log.i("from",""+date2));
            long idPeriod=DBController.addPeriod(getContext(),from.getTimeInMillis(),to.getTimeInMillis(),client.getId());
            if(idPeriod<1){
                Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                return;
            }
            setDisplayedPeriod(idPeriod);
            tv_number.setText(getString(R.string.period_id,idPeriod));
            //List<Day>days=new ArrayList<>();
            Log.i("from",""+new Date(from.getTimeInMillis()));
            Log.i("to",""+new Date(to.getTimeInMillis()));
            Date date;
            days=new ArrayList<>();
            //added an extra day ...the last day
            for (int i=0;i<difference;i++){
                date=new Date(from.getTimeInMillis()+(i*86400000));
                System.out.println(date);
                days.add(DBController.addDay(getContext(),client.getId(),date,0,0,0,idPeriod));
            }
            dayAdapter=new DayAdapter(getContext(),days);
            lv_days.setAdapter(dayAdapter);
            popupWindow.dismiss();
        }
    };
    private View.OnClickListener cashClickListener= v -> {
        long l=DBController.removePeriod(getContext(),getDisplayedPeriod());
        Log.i("vvvvv",""+l);
        if(l>=1){
        popupWindow.dismiss();
        taskIsDoneInterface.recreateFragment();
        }
    };


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_client, container, false);
        vs_toolbar=activity.findViewById(R.id.vs_toolbar);
        btn_options=activity.findViewById(R.id.btn_options);
        TextView tv_toolbar = activity.findViewById(R.id.tv_toolbar);
        btn_options.setOnClickListener(onClickListener);
        tv_from=view.findViewById(R.id.tv_from);
        tv_to=view.findViewById(R.id.tv_to);
        tv_number=view.findViewById(R.id.tv_number);
        ImageView iv_pic = view.findViewById(R.id.iv_pic);
        lv_days =view.findViewById(R.id.lv_days);
        lv_days.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.i("scroll",""+view.requestFocus());
                //  الله أكبر ولله الحمد
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        if(getArguments()!=null) {
            tv_toolbar.setText(client.getName());
            if(client.getThump()!=null)
            iv_pic.setImageBitmap(BitmapFactory.decodeByteArray(client.getThump(),0,client.getThump().length));
            else iv_pic.setImageResource(R.drawable.user3);
            Period period=DBController.getPeriod(getContext(),client.getId());
            setDisplayedPeriod(period.getId());
            days=DBController.getDays(getContext(),client.getId(),period.getId());
            Log.i(TAG,"period id :"+period.getId());
            Log.i(TAG,"days : "+days.size());
            if(period.getId()==0||days.size()==0) {
                tv_number.setText(tv_number.getContext().getResources().getString(R.string.clean_client));
                return view;
            }
            dayAdapter=new DayAdapter(getContext(),days);
            lv_days.setAdapter(dayAdapter);
            dateFormat=new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
            tv_from.setText(getString(R.string.period_from,dateFormat.format(period.getStart())));
            tv_to.setText(getString(R.string.period_to,dateFormat.format(period.getEnd())));
            tv_number.setText(getString(R.string.period_id,period.getId()));

        }
        return view;
    }
}
