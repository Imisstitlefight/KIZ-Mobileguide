package com.example.appfotoaustellung;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appfotoaustellung.data.Area;
import com.example.appfotoaustellung.data.Exhibit;
import com.example.appfotoaustellung.data.ExhibitRepository;
import com.example.appfotoaustellung.data.ProgressStore;

import java.util.ArrayList;
import java.util.List;

/** Liste für den Rundgang: Bereichsüberschriften, darunter die Stücke des Bereichs. */
class TourAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    interface OnExhibitClick {
        void onClick(Exhibit exhibit);
    }

    private static final int TYPE_AREA = 0;
    private static final int TYPE_EXHIBIT = 1;

    /** Enthält abwechselnd Area- und Exhibit-Objekte in Anzeigereihenfolge. */
    private final List<Object> rows = new ArrayList<>();
    private final ExhibitRepository repository;
    private final ProgressStore progressStore;
    private final OnExhibitClick listener;

    TourAdapter(ExhibitRepository repository, ProgressStore progressStore, OnExhibitClick listener) {
        this.repository = repository;
        this.progressStore = progressStore;
        this.listener = listener;
        for (Area area : repository.getAreas()) {
            rows.add(area);
            for (Exhibit exhibit : repository.getExhibits()) {
                if (exhibit.areaId.equals(area.id)) rows.add(exhibit);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof Area ? TYPE_AREA : TYPE_EXHIBIT;
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_AREA) {
            return new AreaHolder(inflater.inflate(R.layout.item_area_header, parent, false));
        }
        return new ExhibitHolder(inflater.inflate(R.layout.item_exhibit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object row = rows.get(position);
        if (holder instanceof AreaHolder) {
            Area area = (Area) row;
            ((AreaHolder) holder).title.setText(area.title);
            ((AreaHolder) holder).description.setText(area.description);
        } else {
            ((ExhibitHolder) holder).bind((Exhibit) row);
        }
    }

    static class AreaHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView description;

        AreaHolder(View view) {
            super(view);
            title = view.findViewById(R.id.text_area_title);
            description = view.findViewById(R.id.text_area_description);
        }
    }

    class ExhibitHolder extends RecyclerView.ViewHolder {
        final TextView number;
        final TextView title;
        final TextView shortText;
        final TextView badge;
        final ImageView check;

        ExhibitHolder(View view) {
            super(view);
            number = view.findViewById(R.id.text_number);
            title = view.findViewById(R.id.text_title);
            shortText = view.findViewById(R.id.text_short);
            badge = view.findViewById(R.id.text_badge);
            check = view.findViewById(R.id.image_visited);
        }

        void bind(Exhibit exhibit) {
            number.setText(String.valueOf(repository.positionOf(exhibit)));
            number.setBackgroundTintList(ColorStateList.valueOf(exhibit.colorInt()));
            title.setText(exhibit.title);
            shortText.setText(exhibit.shortText);
            badge.setVisibility(exhibit.isInteractive() ? View.VISIBLE : View.GONE);
            check.setVisibility(progressStore.isVisited(exhibit.id) ? View.VISIBLE : View.INVISIBLE);
            itemView.setOnClickListener(v -> listener.onClick(exhibit));
        }
    }
}
