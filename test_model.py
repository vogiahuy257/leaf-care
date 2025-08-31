#!/usr/bin/env python3
"""
Test script for LeafCare AI model
Tests the PyTorch model with sample images to understand output format
"""

import torch
import torchvision.transforms as transforms
from PIL import Image
import os
import glob
import numpy as np

def load_model(model_path):
    """Load the PyTorch model"""
    try:
        model = torch.jit.load(model_path, map_location='cpu')
        model.eval()
        print(f"✅ Model loaded successfully from {model_path}")
        return model
    except Exception as e:
        print(f"❌ Error loading model: {e}")
        return None

def preprocess_image(image_path, size=224):
    """Preprocess image for model input"""
    try:
        # Load and resize image
        image = Image.open(image_path).convert('RGB')
        image = image.resize((size, size))
        
        # Convert to tensor and normalize
        transform = transforms.Compose([
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], 
                               std=[0.229, 0.224, 0.225])
        ])
        
        tensor = transform(image).unsqueeze(0)  # Add batch dimension
        return tensor
    except Exception as e:
        print(f"❌ Error preprocessing {image_path}: {e}")
        return None

def test_model(model, image_path):
    """Test model with a single image"""
    try:
        # Preprocess image
        input_tensor = preprocess_image(image_path)
        if input_tensor is None:
            return None
        
        # Run inference
        with torch.no_grad():
            output = model(input_tensor)
        
        # Handle different output formats
        if isinstance(output, tuple):
            output = output[0]
        
        # Get predictions
        probabilities = torch.softmax(output, dim=1)
        predicted_class = torch.argmax(probabilities, dim=1).item()
        confidence = probabilities[0][predicted_class].item()
        
        # Class names mapping
        class_names = ["Bình thường", "Bệnh đốm nâu", "Bệnh phấn trắng", "Bệnh thối rễ"]
        
        result = {
            'image': os.path.basename(image_path),
            'predicted_class': predicted_class,
            'class_name': class_names[predicted_class] if predicted_class < len(class_names) else f"Class {predicted_class}",
            'confidence': confidence,
            'raw_output': output.numpy().flatten(),
            'probabilities': probabilities.numpy().flatten()
        }
        
        return result
        
    except Exception as e:
        print(f"❌ Error testing {image_path}: {e}")
        return None

def main():
    model_path = "leafcare_mbv2.pt"
    
    # Load model
    model = load_model(model_path)
    if model is None:
        return
    
    # Test directories
    test_dirs = {
        "Healthy": "Test/Test/Healthy",
        "Powdery": "Test/Test/Powdery", 
        "Rust": "Test/Test/Rust"
    }
    
    print("\n🔍 Testing model with sample images...")
    print("=" * 60)
    
    all_results = []
    
    for category, dir_path in test_dirs.items():
        if not os.path.exists(dir_path):
            print(f"❌ Directory not found: {dir_path}")
            continue
            
        print(f"\n📁 Testing {category} images:")
        print("-" * 40)
        
        # Get first 3 images from each category
        image_files = glob.glob(os.path.join(dir_path, "*.jpg"))[:3]
        
        for image_file in image_files:
            result = test_model(model, image_file)
            if result:
                print(f"  📸 {result['image']}")
                print(f"     Predicted: {result['class_name']} ({result['confidence']:.3f})")
                print(f"     Raw output shape: {result['raw_output'].shape}")
                print(f"     Raw output values: {result['raw_output'][:5]}...")
                print(f"     Probabilities: {result['probabilities']}")
                print()
                
                all_results.append({
                    'category': category,
                    'result': result
                })
    
    # Summary
    print("\n📊 SUMMARY:")
    print("=" * 60)
    
    for category in test_dirs.keys():
        category_results = [r for r in all_results if r['category'] == category]
        if category_results:
            print(f"\n{category}:")
            for r in category_results:
                result = r['result']
                print(f"  - {result['class_name']} ({result['confidence']:.3f})")
    
    # Check if model output format is consistent
    if all_results:
        first_output = all_results[0]['result']['raw_output']
        print(f"\n🔧 Model output format:")
        print(f"  - Output shape: {first_output.shape}")
        print(f"  - Output range: [{first_output.min():.3f}, {first_output.max():.3f}]")
        print(f"  - Number of classes: {len(first_output)}")

if __name__ == "__main__":
    main()
