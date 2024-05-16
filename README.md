# Birds Image Classifier Application

## Overview

The Birds Image Classifier Application is an Android app that leverages a Convolutional Neural Network (CNN) to classify bird images. Users can select multiple images from their device gallery, and the app will utilize a pre-trained TensorFlow Lite model to identify the bird species in each image.

## Features

- Load multiple images from the device gallery.
- Classify each image using a pre-trained TensorFlow Lite model.
- Display the classification results along with the images.

## Project Structure

### Main Files

- **MainActivity.kt**: The main activity that handles image loading, classification, and displaying results.
- **Modelly.tflite**: The pre-trained TensorFlow Lite model for bird classification.
- **themes.xml**: UI themes for the app.

### Key Components

- **ImageLoaderAndCNN**: A composable function that handles image selection, classification, and UI rendering.
- **classify**: A function that processes the image and performs classification using the TensorFlow Lite model.

## Installation

1. **Clone the Repository**: Clone this repository to your local machine.
2. **Open in Android Studio**: Open the project in Android Studio.
3. **Sync Gradle**: Allow Android Studio to sync Gradle and download necessary dependencies.
4. **TensorFlow Lite Model**: Ensure the TensorFlow Lite model (`Modelly.tflite`) is correctly placed in the `assets` folder.
5. **Build and Run**: Build the project and run it on an Android device or emulator.

## Usage

1. **Launch the App**: Open the Birds Image Classifier application on your Android device.
2. **Select Images**: Click on the "Select multiple photos" button to open the device's gallery and choose images.
3. **View Results**: The app will classify the selected images and display the results along with the images.


OR 
