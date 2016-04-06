package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.CardInfoListAdapter;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.requests.InfoRequest;

public class InfoFragment extends AbstractFragment {

    protected final String HTML_API = "https://zeus.ugent.be/hydra/api/2.0/info/";


    private RecyclerView recyclerView;
    private CardInfoListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_activities, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        adapter = new CardInfoListAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        performLoadInfoRequest();

        return layout;
    }



    private void showFailureSnackbar() {
        Snackbar
                .make(layout, "Oeps! Kon info niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performLoadInfoRequest();
                    }
                })
                .show();
    }


    private void performLoadInfoRequest() {
        final InfoRequest r = new InfoRequest();

        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<InfoList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final InfoList infolist) {
                adapter.setItems(infolist);
                adapter.notifyDataSetChanged();
            }
        });


    }
   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        performLoadInfoRequest();
        return view;
    }

    private void performLoadInfoRequest() {

        final InfoRequest r = new InfoRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<InfoList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                System.out.println("Request failed");
            }

            @Override
            public void onRequestSuccess(final InfoList infolist) {
                final ArrayList<InfoItem> listItems = new ArrayList<>();
                ArrayAdapter<InfoItem> adapter;
                final ListView activityList = (ListView) getView().findViewById(R.id.infoList);
                adapter = new InfoListAdapter(getContext(), android.R.layout.simple_list_item_1, listItems);
                activityList.setAdapter(adapter);

                activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        InfoItem item = infolist.get(position);
                        String url = item.getUrl();
                        String html = item.getHtml();
                        InfoList infolist = item.getSubContent();
                        if (url != null) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        } else if (html != null ){
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HTML_API + html));
                            startActivity(browserIntent);
                        } else if (infolist != null) {
                            //TODO view subcontent
                            for (InfoItem subcontentitem : infolist) {
                            }
                        }
                    }
                });

                for (InfoItem item : infolist) {
                    listItems.add(item);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    */
}
