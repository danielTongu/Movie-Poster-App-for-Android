package com.example.movieposterappforandroid;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MainActivity for managing the main movie poster screen, including
 * a list of movies, search functionality, and a watchlist feature.
 *
 * @author Daniel Tongu
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView posterRecyclerView;
    private PosterAdapter posterAdapter;
    private Button watchlistButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private List<Movie> movieList;
    private Set<Movie> watchlist = new HashSet<>();
    private boolean isInWatchlistView = false;

    /**
     * Initializes the activity, including the toolbar, drawer, RecyclerView, and watchlist functionality.
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView menuIcon = findViewById(R.id.menuIcon);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.toolbarTitle)).setText("All Posters");

        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        searchIcon.setOnClickListener(v -> openSearchDialog());

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_all_movies) {
                switchToMoviesView();
            } else if (item.getItemId() == R.id.nav_watchlist) {
                switchToWatchlistView();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        posterRecyclerView = findViewById(R.id.posterRecyclerView);
        posterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        watchlistButton = findViewById(R.id.watchlistButton);
        watchlistButton.setVisibility(View.GONE);

        movieList = getMovieList();
        posterAdapter = new PosterAdapter(movieList, watchlist, this::onMovieSelected);
        posterRecyclerView.setAdapter(posterAdapter);

        watchlistButton.setOnClickListener(v -> {
            if (isInWatchlistView) {
                posterAdapter.getSelectedMovies().forEach(movie -> {
                    watchlist.remove(movie);
                });
                Toast.makeText(this, "Removed from Watchlist", Toast.LENGTH_SHORT).show();
            } else {
                posterAdapter.getSelectedMovies().forEach(movie -> {
                    watchlist.add(movie);
                });
                Toast.makeText(this, "Added to Watchlist", Toast.LENGTH_SHORT).show();
            }
            updateListView();
            watchlistButton.setVisibility(View.GONE);
        });
    }

    /**
     * Controls the visibility and text of the watchlist button based on movie selection.
     * @param isAnySelected True if any movie is selected, false otherwise.
     */
    private void onMovieSelected(boolean isAnySelected) {
        watchlistButton.setVisibility(isAnySelected ? View.VISIBLE : View.GONE);
        watchlistButton.setText(isInWatchlistView ? "Remove from Watchlist" : "Add to Watchlist");
        watchlistButton.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor));
        watchlistButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        watchlistButton.setTextSize(25);
    }
    /**
     * Switches the displayed list to show all movies.
     */
    private void switchToMoviesView() {
        isInWatchlistView = false;
        ((TextView) findViewById(R.id.toolbarTitle)).setText("All Posters");
        posterAdapter.updateList(movieList);
    }

    /**
     * Switches the displayed list to show only watchlisted movies.
     */
    private void switchToWatchlistView() {
        isInWatchlistView = true;
        ((TextView) findViewById(R.id.toolbarTitle)).setText("My Watchlist");
        posterAdapter.updateList(new ArrayList<>(watchlist));
    }

    /**
     * Updates the current list view based on the current view (all movies or watchlist).
     */
    private void updateListView() {
        posterAdapter.clearSelection();
        posterAdapter.updateList(isInWatchlistView ? new ArrayList<>(watchlist) : movieList);
    }

    /**
     * Opens a dialog for searching movies in the current list.
     */
    private void openSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search Movies");

        EditText input = new EditText(this);
        input.setHint("Type to search...");
        input.setSingleLine();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                posterAdapter.filter(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        builder.setView(input)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create().show();
    }

    /**
     * Creates and returns a sample list of movies with placeholder data.
     * @return List of sample Movie objects.
     */
    private List<Movie> getMovieList() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("Inception", "Christopher Nolan", "A thief with the ability to enter people’s dreams and steal secrets from their subconscious is given a chance to erase his past crimes by planting an idea in someone's mind.", R.drawable.inception, 4.8f));
        movies.add(new Movie("The Dark Knight", "Christopher Nolan", "Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice as he faces the Joker, a criminal mastermind who wants to plunge Gotham into anarchy.", R.drawable.dark_knight, 4.9f));
        movies.add(new Movie("Interstellar", "Christopher Nolan", "A group of explorers travel through a wormhole in space in an attempt to ensure humanity's survival on a new planet.", R.drawable.interstellar, 4.7f));
        movies.add(new Movie("Parasite", "Bong Joon-ho", "Greed and class discrimination threaten the relationship between the wealthy Park family and the impoverished Kim family in this award-winning thriller.", R.drawable.parasite, 4.6f));
        movies.add(new Movie("The Matrix", "The Wachowskis", "A hacker discovers the reality he's living in is a simulated reality controlled by machines, and joins a group of rebels to fight for freedom.", R.drawable.matrix, 4.8f));
        movies.add(new Movie("Forrest Gump", "Robert Zemeckis", "The life journey of Forrest Gump, a man with a low IQ, who inadvertently influences many historical events in his search for love and self-fulfillment.", R.drawable.forrest_gump, 4.7f));
        movies.add(new Movie("The Shawshank Redemption", "Frank Darabont", "The story of a banker who is sentenced to life in prison for the murders of his wife and her lover, and his journey of redemption within the prison walls.", R.drawable.shawshank_redemption, 4.9f));
        movies.add(new Movie("The Godfather", "Francis Ford Coppola", "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.", R.drawable.godfather, 4.9f));
        movies.add(new Movie("Pulp Fiction", "Quentin Tarantino", "The lives of two mob hitmen, a boxer, a gangster and his wife intertwine in this darkly comedic thriller.", R.drawable.pulp_fiction, 4.7f));
        movies.add(new Movie("Gladiator", "Ridley Scott", "A betrayed Roman general seeks revenge on the emperor who killed his family and sent him into slavery.", R.drawable.gladiator, 4.5f));

        return movies;
    }
}

/**
 * Represents a movie with details such as name, author, description, image resource, and rating.
 * @author Daniel Tongu
 */
class Movie {
    private final String name;
    private final String author;
    private final String description;
    private final int imageResId;
    private final float rating;

    /**
     * Creates a Movie object.
     * @param name        Name of the movie.
     * @param author      Author or director of the movie.
     * @param description Brief description of the movie.
     * @param imageResId  Resource ID for the movie's image.
     * @param rating      User rating of the movie.
     */
    public Movie(String name, String author, String description, int imageResId, float rating) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.imageResId = imageResId;
        this.rating = rating;
    }

    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getDescription() { return description; }
    public int getImageResId() { return imageResId; }
    public float getRating() { return rating; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return name.equals(movie.name) && author.equals(movie.author);
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + author.hashCode();
    }
}


/**
 * Adapter class for displaying movie posters in a RecyclerView.
 * @author Daniel Tongu
 */
class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {
    private final List<Movie> movieList;
    private final List<Movie> filteredMovieList;
    private final SelectionCallback selectionCallback;
    private final List<Movie> selectedMovies = new ArrayList<>();

    private final Set<Movie> watchlist;  // Add this line

    /**
     * Constructs a PosterAdapter.
     * @param movieList        List of movies to display.
     * @param watchlist        Set of movies in the watchlist.
     * @param selectionCallback Callback for handling selection changes.
     */
    public PosterAdapter(List<Movie> movieList, Set<Movie> watchlist, SelectionCallback selectionCallback) {
        this.movieList = movieList;
        this.filteredMovieList = new ArrayList<>(movieList);
        this.watchlist = watchlist;  // Store the watchlist reference
        this.selectionCallback = selectionCallback;
    }

    @NonNull
    @Override
    public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_item, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
        Movie movie = filteredMovieList.get(position);
        holder.movieName.setText(movie.getName());
        holder.movieAuthor.setText(movie.getAuthor());
        holder.movieDescription.setText(movie.getDescription());
        holder.ratingBar.setRating(movie.getRating());
        holder.movieImage.setImageResource(movie.getImageResId());
        holder.checkmarkOverlay.setVisibility(watchlist.contains(movie) ? View.VISIBLE : View.GONE);

        // Set background color based on selection or watchlist status
        if (selectedMovies.contains(movie)) {
            holder.moviePoster.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primaryColor));
        } else {
            holder.moviePoster.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorMediumGray));
        }

        // Toggle selection on click (doesn't affect the actual watchlist)
        holder.itemView.setOnClickListener(v -> {
            if (selectedMovies.contains(movie)) {
                selectedMovies.remove(movie);
                holder.moviePoster.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorMediumGray));
            } else {
                selectedMovies.add(movie);
                holder.moviePoster.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primaryColor));
            }
            selectionCallback.onSelectionChanged(!selectedMovies.isEmpty());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return filteredMovieList.size();
    }

    /**
     * Updates the adapter's data with a new movie list.
     * @param newMovieList New list of movies to display.
     */
    public void updateList(List<Movie> newMovieList) {
        filteredMovieList.clear();
        filteredMovieList.addAll(newMovieList);
        notifyDataSetChanged();
    }

    /**
     * Returns a list of currently selected movies.
     * @return List of selected Movie objects.
     */
    public List<Movie> getSelectedMovies() { return new ArrayList<>(selectedMovies); }

    /**
     * Clears the selection of movies.
     */
    public void clearSelection() {
        selectedMovies.clear();
    }

    /**
     * Filters the displayed movies based on a query string.
     * @param query Search query to filter movies.
     */
    public void filter(String query) {
        filteredMovieList.clear();
        if (query.isEmpty()) {
            filteredMovieList.addAll(movieList);
        } else {
            for (Movie movie : movieList) {
                if (movie.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredMovieList.add(movie);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Callback interface for handling selection changes in the RecyclerView.
     */
    public interface SelectionCallback {
        /**
         * Called when the selection changes.
         * @param isAnySelected True if any items are selected, false otherwise.
         */
        void onSelectionChanged(boolean isAnySelected);
    }

    /**
     * ViewHolder for displaying movie poster details.
     * @author Daniel Tongu
     */
    public static class PosterViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout moviePoster;
        ImageView movieImage;
        LinearLayout movieDetails;
        TextView movieName;
        RatingBar ratingBar;
        TextView movieAuthor;
        TextView movieDescription;
        ImageView checkmarkOverlay;

        public PosterViewHolder(@NonNull View itemView) {

            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            moviePoster = itemView.findViewById(R.id.moviePoster);
            movieImage = itemView.findViewById(R.id.movieImage);
            movieDetails = itemView.findViewById(R.id.movieDetails);
            movieName = itemView.findViewById(R.id.movieName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            movieAuthor = itemView.findViewById(R.id.movieAuthor);
            movieDescription = itemView.findViewById(R.id.movieDescription);
            checkmarkOverlay = itemView.findViewById(R.id.checkmarkOverlay);
        }
    }
}