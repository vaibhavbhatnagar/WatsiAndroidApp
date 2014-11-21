/***

The MIT License (MIT)
Copyright � 2014 Rutvijkumar Shah
 
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the �Software�), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.
 
THE SOFTWARE IS PROVIDED �AS IS�, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 ***/

package codepath.watsiapp.fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import codepath.watsiapp.R;
import codepath.watsiapp.api.Services;
import codepath.watsiapp.interfaces.OnDonationStatsCalculatedListener;
import codepath.watsiapp.modelsv2.DonationsResponse;
import codepath.watsiapp.utils.EndlessScrollListener;

import com.activeandroid.util.Log;

import eu.erikw.PullToRefreshListView;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

public class DonationListFragment extends Fragment {

	private codepath.watsiapp.adaptersv2.DonationAdapter feedAdapter;
	private eu.erikw.PullToRefreshListView listView;
	private ProgressBar progressBar;
	private String donorId;
	private static final String DONOR_ID = "DONOR_ID";
	private OnDonationStatsCalculatedListener listener;

	public static final int PAGE_SIZE = 20;
	private ArrayList<codepath.watsiapp.modelsv2.Donation> newsItems=new ArrayList<codepath.watsiapp.modelsv2.Donation>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		String donor__id = bundle.getString(DONOR_ID);
		this.donorId = donor__id;
		feedAdapter=new codepath.watsiapp.adaptersv2.DonationAdapter(getActivity(), newsItems);
	}

	@Override
	public void onAttach(Activity activity) {
		listener = (OnDonationStatsCalculatedListener) activity;
		super.onAttach(activity);
	}

	
	private void calculateTotalDonation(List<codepath.watsiapp.modelsv2.Donation> donations){
		double totalDonations = 0.00;
		final Set<String> treatments = new HashSet<String>();
		for (codepath.watsiapp.modelsv2.Donation donation : donations) {
			totalDonations += donation.getDonationAmount();
			treatments.add(donation.getPatient()
					.getObjectId());
		}
		listener.totalDonationsCalculated(totalDonations);
		listener.totalTreatmentsCalculated(treatments
				.size());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_donation_list, container,
				false);

		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		
		listView = (PullToRefreshListView) v.findViewById(R.id.donation_list);
		listView.setAdapter(feedAdapter);
		setupIintialViews();
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupIintialViews();
	}

	private void setupIintialViews() {

		listView.setOnScrollListener(new EndlessScrollListener() {
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				if(totalItemsCount > 0) {
					populateData(PAGE_SIZE,totalItemsCount);
					
				}
			}

		});
		listView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				progressBar.setVisibility(View.VISIBLE);
				feedAdapter.clear();
				
				populateData(PAGE_SIZE,0,true);
			}
		});
	}


	private void populateData(int pageSize,int skip) {
		populateData(pageSize,skip,false);
	}
	private void populateData(final int pageSize,final int skip,final boolean isPulledToRefresh) {
		
		Services.getInstance().getDonationService().findtDonationById(donorId, new Callback<DonationsResponse>() {

			@Override
			public void failure(RetrofitError err) {

				Log.e("Error while loading page :"+pageSize+" and skip="+skip,err);
				Toast.makeText(getActivity(), "Error:"+err, Toast.LENGTH_LONG).show();
				progressBar.setVisibility(View.INVISIBLE);
				
			}

			@Override
			public void success(DonationsResponse donationResponse, Response arg1) {
				if(isPulledToRefresh) {
					listView.onRefreshComplete();
				}
				calculateTotalDonation(donationResponse.results);
				feedAdapter.addAll(donationResponse.results);
				feedAdapter.notifyDataSetChanged();
				progressBar.setVisibility(View.INVISIBLE);
				
				
			}
			
		} );
		

		
	}
	public static DonationListFragment newInstance(String donorId) {
		DonationListFragment fragment = new DonationListFragment();
		Bundle args = new Bundle();
		args.putString(DONOR_ID, donorId);
		fragment.setArguments(args);
		return fragment;
	}

}
