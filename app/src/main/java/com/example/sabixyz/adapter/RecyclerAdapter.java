package com.example.sabixyz.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sabixyz.BookDetailsFragment;
import com.example.sabixyz.R;
import com.example.sabixyz.model.ListItem;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    private List<ListItem> listitems;
    private Context context;

    public RecyclerAdapter(List<ListItem> listitems, Context context) {
        this.listitems = listitems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final ListItem listItem = listitems.get(i);
        holder.textViewHead.setText(listItem.getHead());
        holder.textViewAuthor.setText(listItem.getAuthor());
        holder.BookID = listItem.getId();
        holder.list = listItem;
        Picasso.get().load("http://172.20.10.3/sabi/"+listItem.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView textViewHead;
        public TextView textViewAuthor;
        public ImageView imageView;
        public String BookID;
        public ListItem list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewHead = itemView.findViewById(R.id.textViewHead);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            imageView = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View view)
        {
            // get the position on recyclerview.
            int pos = getLayoutPosition();


            SingleItemClick(view, pos, list);    // click on recyclerview item.
        }

        private void SingleItemClick(View view, int pos, ListItem listItem) {
            Log.e("Item", "Itemclicked"+pos);

            Bundle b = new Bundle();
            b.putString("id", list.getId());
            b.putString("description", list.getDescription());
            b.putString("imageurl", list.getImageUrl());
            b.putString("title", list.getHead());
            b.putString("author",list.getAuthor());
            b.putString("amount", list.getAmount());
            b.putString("list", list.toString());
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment myFragment = new BookDetailsFragment();
            myFragment.setArguments(b);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).addToBackStack(null).commit();

            /*
            FragmentManager fragmentManager = .getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragmentselected);
            fragmentTransaction.commit();
            fragmentManager.popBackStack();
            */
        }
    }
}
