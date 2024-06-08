from flask import Flask, request, jsonify
import tempfile
import os
import torch
import numpy as np
import librosa

app = Flask(__name__)

# Load the model
model_path = "D://University//Kirollous Hany//4th-Year//GradProject//Det Model//detectionModel.pth"
loaded_object = torch.load(model_path)
loaded_object.eval()

# Move model to GPU if available
device = torch.device('cuda' )
loaded_object.to(device)

# Print the device of model parameters to ensure they are on the correct device
for param in loaded_object.parameters():
    print(param.device)

# Function to preprocess audio
def preprocess_audio(audio_path, sequence_length=17240):
    audio, sr = librosa.load(audio_path, sr=None)
    audio /= np.max(np.abs(audio))
    audio_tensor = torch.tensor(audio, dtype=torch.float32).unsqueeze(0).unsqueeze(1)
    if audio_tensor.shape[2] < sequence_length:
        audio_tensor = torch.nn.functional.pad(audio_tensor, (0, sequence_length - audio_tensor.shape[2]))
    else:
        audio_tensor = audio_tensor[:, :, :sequence_length]
    return audio_tensor.permute(0, 2, 1)

# API endpoint for prediction
@app.route('/predict', methods=['POST'])
def predict():
    # Check if audio file is present in request
    if 'file' not in request.files:
        return jsonify({'error': 'No file part'})

    file = request.files['file']

    # Save the audio file to a temporary location
    with tempfile.NamedTemporaryFile(delete=False, suffix='.wav') as temp_audio:
        temp_audio_path = temp_audio.name
        file.save(temp_audio_path)

    try:
        # Preprocess the audio
        input_data = preprocess_audio(temp_audio_path)

        # Move input data to GPU if available
        input_data = input_data.to(device)

        # Ensure input data is on the correct device
        print(input_data.device)

        # Make prediction
        with torch.no_grad():
            output = loaded_object(input_data)

        # Convert the output to probabilities using sigmoid activation
        probabilities = torch.sigmoid(output)

        # Extract the predicted class (0 for fake, 1 for real)
        predicted_class = (probabilities > 0.5).float()

        # Return the prediction result
        return jsonify({
            'probabilities': probabilities.item(),
            'predicted_class': 'Fake' if predicted_class.item() == 1 else 'Real'
        })
    finally:
        # Delete the temporary audio file
        os.unlink(temp_audio_path)

if __name__ == '__main__':
    app.run(debug=True, host='192.168.1.5',port=1)
