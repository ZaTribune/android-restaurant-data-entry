package shadow.android.data_entry_1;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import shadow.android.data_entry_1.db.Client;
import shadow.android.data_entry_1.db.DBController;

public class ClientIconAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Client> clients;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public ClientIconAdapter(Context context){
      this.inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      this.clients = DBController.getClients(context);
    }

    public void refreshNoteIcons() {
        clients =DBController.getClients(context);
    }

    @Override
    public int getCount() {
        return clients.size();
    }

    @Override
    public Client getItem(int position) {
        return clients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    final class ViewHolder{
        TextView tv_title;
        ImageView iv_thumb;
        CheckBox cbox_select;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView=inflater.inflate(R.layout.client_icon,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.tv_title=convertView.findViewById(R.id.tv_title);
            viewHolder.iv_thumb=convertView.findViewById(R.id.iv_thump);
            viewHolder.cbox_select=convertView.findViewById(R.id.cbox_select);
            convertView.setTag(viewHolder);

        }else {
           viewHolder= (ViewHolder)convertView.getTag();
        }
        viewHolder.tv_title.setText(clients.get(position).getName());
        if(clients.get(position).getThump()!=null)
        viewHolder.iv_thumb.setImageBitmap(BitmapFactory.decodeByteArray(clients.get(position).getThump(),
                0, clients.get(position).getThump().length));
        else viewHolder.iv_thumb.setImageResource(R.drawable.user3);
        return convertView;
    }
}
