package dab.scuffedbots;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Switch messageSwitch;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.display);
            messageSwitch = (Switch) itemView.findViewById(R.id.ticker);
        }
    }


    // Store a member variable for the contacts
    private List<Parameters.ListViewItem> mitems;

    // Pass in the contact array into the constructor
    public ContactsAdapter(List<Parameters.ListViewItem> items) {
        mitems = items;
    }



    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.parameterspagerow, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Parameters.ListViewItem item = mitems.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        //textView.setText(item.PARAMETER);
        Switch switcher = viewHolder.messageSwitch;
        if(item.CHECKED.equals("yes")){
            switcher.setChecked(true); }
        else if(item.CHECKED.equals("no")){
            switcher.setChecked(false); }

        if(Parameters.selectedlanguego.equals("english")){
        if(position==0)
            textView.setText("Hide camera by default");
        if(position==1)
            textView.setText("Minimize menu by default");
        if(position==2)
            textView.setText("Helper Voice instructions");
        if(position==3)
            textView.setText("Speak outloud detected items");
        if(position==4)
            textView.setText("Disable all voices");
        if(position==5)
            textView.setText("Skip intro page");
        if(position==6)
            textView.setText("Show confidence");
        } else if(Parameters.selectedlanguego.equals("french")){
            if(position==0)
                textView.setText("Cacher la camera par d\u00E9faut");
            if(position==1)
                textView.setText("Cacher le menu par d\u00E9faut");
            if(position==2)
                textView.setText("Intructions vocales");
            if(position==3)
                textView.setText("\u00C9peler les objets d\u00E9t\u00E9ctes");
            if(position==4)
                textView.setText("D\u00E9sactiver audios");
            if(position==5)
                textView.setText("Sauter la page de s\u00E9l\u00E9ction de langues");
            if(position==6)
                textView.setText("Afficher les pourcentages de confidence");
        } else if(Parameters.selectedlanguego.equals("arabic")){
            if(position==0)
                textView.setText(viewHolder.itemView.getContext().getString(R.string.setting1));
            if(position==1)
                textView.setText(viewHolder.itemView.getContext().getString(R.string.setting2));
            if(position==2)
                textView.setText(viewHolder.itemView.getContext().getString(R.string.setting3));
            if(position==3)
                textView.setText(viewHolder.itemView.getContext().getString(R.string.setting4));
            if(position==4)
                textView.setText(viewHolder.itemView.getContext().getString(R.string.setting5));
            if(position==5)
                textView.setText(viewHolder.itemView.getContext().getString(R.string.setting6));
            if(position==6)
                textView.setText(viewHolder.itemView.getContext().getString(R.string.setting7));
        }

        textView.setTextColor(Color.parseColor("#FFFFFF"));

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    nignog.mydb.updateData(String.valueOf(position+1), "yes"); }
                else
                    nignog.mydb.updateData(String.valueOf(position+1), "no");
            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mitems.size();
    }
}