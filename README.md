Overview
This project is a news app that consists of 4 main fragments:

Recent News
Favorite News
Search Screen
Detail Screen
The application launches with the Recent News screen as the starting point. Users can navigate between the Recent News, Favorite News, and Search screens via the bottom menu. When a news item is clicked, the user is directed to the Detail Screen.

Since the API response does not provide a detailed description of the article beyond what is shown in the list, the Detail Screen displays the article using a WebView with the provided article link. On the Detail Screen, users can add or remove articles from their favorites using the floating action button in the bottom right corner. Favorited articles are then listed in the Favorite News fragment.

API Handling
For API calls, there was no need to manually add a query parameter for "ordering" since the default response provides the most recent news. Therefore, only the offset and limit parameters were used (with an additional search parameter for the Search Screen).

Offline Mode
The app also supports offline mode. In the Recent News fragment, as the user scrolls through the news feed, articles are saved locally in a Room database, with a limit of 50 articles being stored.If the user has an internet connection, the local database is cleared and refreshed with new data as the user scrolls.

For the Search Screen, the search functionality is not available for offline mode. If the user attempts to search without an internet connection, a toast message will notify them of the lack of connectivity.
