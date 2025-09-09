package com.example.guiamapas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {

    private List<String> categorias;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String categoria);
    }

    public CategoriaAdapter(List<String> categorias, OnItemClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    @Override
    public CategoriaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriaViewHolder holder, int position) {
        holder.textView.setText(categorias.get(position));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        CategoriaViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(categorias.get(getAdapterPosition()));
                }
            });
        }
    }
}
