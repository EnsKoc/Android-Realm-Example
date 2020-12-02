package com.example.realmdbexample.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.realmdbexample.Interfaces.IClickListener;
import com.example.realmdbexample.Model.PersonTable;
import com.example.realmdbexample.R;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private List<PersonTable> personList;
    private IClickListener clickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView personName, personSurname, personDepartment, personAge, personOptionMenu;
        public MyViewHolder(View view) {
            super(view);
            personName = (TextView) view.findViewById(R.id.personName);
            personSurname = (TextView) view.findViewById(R.id.personSurname);
            personDepartment = (TextView) view.findViewById(R.id.personDepartment);
            personAge = (TextView) view.findViewById(R.id.personAge);
            personOptionMenu = (TextView)view.findViewById(R.id.textViewOptions);

            personOptionMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onMenuClick(v,getAdapterPosition());
                }
            });
        }
    }

    public CustomAdapter(Context mContext, List<PersonTable> personList, IClickListener clickListener) {
        this.context = mContext;
        this.personList = personList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recylerview_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        PersonTable personTable = personList.get(position);
        holder.personName.setText(personTable.getName());
        holder.personSurname.setText(personTable.getSurname());
        holder.personDepartment.setText(personTable.getDepartment());
        holder.personAge.setText(String.valueOf(personTable.getAge()));
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }
}