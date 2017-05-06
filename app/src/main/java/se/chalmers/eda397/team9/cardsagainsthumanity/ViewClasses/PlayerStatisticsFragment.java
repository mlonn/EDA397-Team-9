package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import se.chalmers.eda397.team9.cardsagainsthumanity.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlayerStatisticsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlayerStatisticsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerStatisticsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /* Class variables */
    private List<PlayerRowLayout> playerRowList;
    private PlayerInfo hostInfo;
    private TableInfo myTableInfo;

    /* Views */
    private PlayerRowLayout hostRow;
    private GridLayout playerGridLayout;

    /* Colors */
    private String[] colorArray = {
            "#f8c82d", "#fbcf61", "#ff6f6f",
            "#e3a712", "#e5ba5a", "#d1404a",
            "#0dccc0", "#a8d164", "#3498db",
            "#0ead9a", "#27ae60", "#2980b9",
            "#d49e99", "#b23f73", "#48647c",
            "#74525f", "#832d51", "#2c3e50",
            "#e84b3a", "#fe7c60", "#ecf0f1",
            "#c0392b", "#404148", "#bdc3c7"};

    private LinkedList<String> colorList;

    /* Not sure what this is */
    private OnFragmentInteractionListener mListener;

    public PlayerStatisticsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerStatisticsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerStatisticsFragment newInstance(String param1, String param2) {
        PlayerStatisticsFragment fragment = new PlayerStatisticsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /* Currently not used for anything */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player_statistics, container, false);

        /* Get intent data */
        myTableInfo = (TableInfo) getActivity().getIntent().getSerializableExtra(IntentType.THIS_TABLE);

        /* Initialize variables */
        playerRowList = new ArrayList<>();
        playerGridLayout = (GridLayout) rootView.findViewById(R.id.playerlist_grid);
        colorList = new LinkedList<>(Arrays.asList(colorArray));
        hostInfo = myTableInfo.getHost();

        return rootView;
    }

    /* Adds player to the playerlist
    *  Note: This is used locally during onCreateView, and can thus
    *  not be combined with the public version. */
    public PlayerRowLayout addPlayer(PlayerInfo newPlayer){
        if(PlayerInfo.findPlayerInList(myTableInfo.getPlayerList(), newPlayer) == null){
            myTableInfo.addPlayer(newPlayer);
        }

        if(findPlayerRow(playerRowList, newPlayer) == null) {
            if (newPlayer.getColor() == null) {
                assignRandomColor(newPlayer);
            }

            PlayerRowLayout playerRow = new PlayerRowLayout(getContext());
            playerRow.setName(newPlayer.getName());
            playerRow.setImageColor(newPlayer.getColor());
            playerRow.setPlayerId(newPlayer.getDeviceAddress());
            playerRow.setScore(newPlayer.getScore());

            //Gives the player the crown
            if (newPlayer.isKing()) {
                playerRow.setKing();
            }

            playerRowList.add(playerRow);
            playerGridLayout.addView(playerRow);

            return playerRow;
        }
        return null;
    }

    /* Adds host to the playerlist
    *  NOTE: Cannot be combined with the public version. */
    private void addHost(PlayerInfo hostInfo, View view){
        if(hostInfo.getColor().equals("#000000")){
            assignRandomColor(hostInfo);
        }
        hostRow = addHostRow(hostInfo);
    }

    public void addHost(PlayerInfo hostInfo){
        addHost(hostInfo, getView());
    }

    /* Helper method for finding playerRow based on player*/
    private PlayerRowLayout findPlayerRow(List<PlayerRowLayout> list, PlayerInfo player){
        for(PlayerRowLayout current : list){
            if(current.getPlayerId() != null && current.getPlayerId().equals(player.getDeviceAddress()))
                return current;
        }
        return null;
    }

    /* Adds a host row */
    private PlayerRowLayout addHostRow(PlayerInfo playerInfo){
        PlayerRowLayout hostRow = new PlayerRowLayout(getContext());
        hostRow.setName(playerInfo.getName());
        hostRow.setAsHost();
        hostRow.setImageColor(playerInfo.getColor());
        hostRow.setScore(playerInfo.getScore());
        if (playerInfo.isKing()) { hostRow.setKing();}
        playerGridLayout.addView(hostRow);

       /* Just for testing
        //Sets the host as king to start off with
        playerInfo.setKing();
        hostRow.setKing();
        */

        return hostRow;
    }

    /* Assigns random color to a player */
    private void assignRandomColor(PlayerInfo playerInfo){
        int randomNumber = (int) (Math.random() * colorList.size());
        String color = colorList.get(randomNumber);
        colorList.remove(randomNumber);
        playerInfo.setColor(color);
    }

    /* Currently not used */

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void initializePlayers(TableInfo tableInfo){
        if(playerRowList.isEmpty()) {
            for (PlayerInfo current : tableInfo.getPlayerList()) {
                PlayerRowLayout currentRow = addPlayer(current);
                currentRow.setColor(current.getColor());
            }
        }
    }

    public void update(TableInfo tableInfo) {
        //TODO: This might need to be more complex, such as looking into the
        //TODO: class and notice differences in properties
        for(PlayerInfo current : tableInfo.getPlayerList()){
            PlayerRowLayout currentRow = findPlayerRow(playerRowList, current);
            if(currentRow == null){
                addPlayer(current);
                currentRow = findPlayerRow(playerRowList, current);
            }
            if(!currentRow.getColor().equals(current.getColor())){
                currentRow.setColor(current.getColor());
            }
            //TODO: Check score and king as well when implemented
        }
    }

    public void removePlayer(final PlayerInfo player) {
        android.os.Handler mainHandler = new android.os.Handler(getActivity().getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                playerGridLayout.removeView(findPlayerRow(playerRowList, player));
                playerRowList.remove(findPlayerRow(playerRowList, player));
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
