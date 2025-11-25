# SentiVibe Android App

SentiVibe is an Android application that analyzes the sentiment of a given text using a Python backend. This document provides all the necessary information to set up and run the project.

## Features

-   **Text Analysis:** Enter any text and get a sentiment score.
-   **Dual-Model Analysis:** Utilizes both VADER and TextBlob for a more comprehensive analysis.
-   **Dynamic Charting:** The VADER compound score is plotted on a line chart for each analysis.
-   **Modern UI:** The app uses Material Design components for a clean and intuitive user interface.
-   **Configurable Builds:** The app can be configured to point to a local server for development or a production server for release.

## Project Structure

The project follows a clean architecture pattern:

-   `data`: Contains the data models and networking logic (Retrofit).
-   `network`: Although the Retrofit client is in the `data` package, this is where you would put additional networking components.
-   `ui`: This is implicitly handled by `MainActivity` and `activity_main.xml`.

## How to Set Up and Run the Android App

1.  **Clone the Repository:**
    ```sh
    git clone <your-repository-url>
    ```

2.  **Open in Android Studio:**
    -   Open Android Studio.
    -   Select "Open an existing Android Studio project".
    -   Navigate to the cloned repository and select the `SentiVibe` folder.

3.  **Sync Gradle:**
    -   Android Studio should automatically sync the Gradle files. If not, click the "Sync Project with Gradle Files" button in the toolbar.

4.  **Run the App:**
    -   You can run the app on an Android emulator or a physical device.
    -   Select your target device from the dropdown menu and click the "Run" button.

## Backend Setup (Python Flask)

To run the backend, you will need Python and Flask installed.

1.  **Install Dependencies:**

    ```sh
    pip install Flask flask-cors vaderSentiment textblob
    ```

2.  **Create the Flask App:**

    Create a file named `app.py` with the following content:

    ```python
    from flask import Flask, request, jsonify
    from flask_cors import CORS
    from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer
    from textblob import TextBlob

    app = Flask(__name__)
    CORS(app)  # Enable CORS for all routes

    vader_analyzer = SentimentIntensityAnalyzer()

    @app.route('/score', methods=['POST'])
    def get_sentiment_score():
        data = request.get_json()
        if not data or 'text' not in data:
            return jsonify({'error': 'Invalid input, "text" field is required.'}), 400

        text_to_analyze = data['text']

        # VADER Analysis
        vader_scores = vader_analyzer.polarity_scores(text_to_analyze)

        # TextBlob Analysis
        blob = TextBlob(text_to_analyze)
        textblob_scores = {
            'polarity': blob.sentiment.polarity,
            'subjectivity': blob.sentiment.subjectivity
        }

        response = {
            'vader': vader_scores,
            'textblob': textblob_scores
        }

        return jsonify(response)

    if __name__ == '__main__':
        app.run(host='0.0.0.0', port=5001, debug=True)

    ```

3.  **Run the Backend:**

    ```sh
    python app.py
    ```

    The backend will now be running at `http://0.0.0.0:5001/`.

## Troubleshooting

-   **Android App Fails to Connect:**
    -   If you are using an emulator, the `debug` build is configured to connect to `http://10.0.2.2:5001/`. Make sure your backend is running.
    -   If you are using a physical device, you need to be on the same Wi-Fi network as the machine running the backend. Find the local IP address of your machine (e.g., `192.168.1.100`) and update the `API_BASE_URL` in the `app/build.gradle.kts` file for the `debug` build type, then sync Gradle.

-   **Gradle Sync Fails:**
    -   Ensure you have a stable internet connection.
    -   Try cleaning the project (`Build` > `Clean Project`) and rebuilding.

-   **Python Dependencies Not Found:**
    -   Make sure you have installed the required Python packages using `pip`.

## Workflow Diagram

```
+-----------------------+
| Android App (SentiVibe) |
+-----------+-----------+
            |
            | 1. User enters text and clicks "Analyze"
            v
+-----------+-----------+
|   MainActivity.java   |
+-----------+-----------+
            |
            | 2. Makes a POST request to the backend
            v
+-----------+-----------+
|  RetrofitClient.java  |
+-----------+-----------+
            |
            | 3. Sends request over the network
            v
+-----------------------+
| Flask Backend (app.py)  |
+-----------+-----------+
            |
            | 4. Analyzes text with VADER and TextBlob
            v
+-----------+-----------+
|      JSON Response      |
+-----------+-----------+
            |
            | 5. Sends sentiment scores back to the app
            v
+-----------+-----------+
|   MainActivity.java   |
+-----------+-----------+
            |
            | 6. Parses the response and updates the UI
            v
+-----------+-----------+
|  TextView & LineChart |
+-----------------------+
```
