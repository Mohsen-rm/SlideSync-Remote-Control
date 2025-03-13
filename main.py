from flask import Flask, request, jsonify
import pyautogui

app = Flask(__name__)

@app.route('/', methods=['GET'])
def index():
    return jsonify({"AppSlideSyncRemote": True, "port": 57875})

@app.route('/next_slide', methods=['POST'])
def next_slide():
    pyautogui.press('right')
    return jsonify({"status": "success", "message": "Moved to next slide"}), 200

@app.route('/prev_slide', methods=['POST'])
def prev_slide():
    pyautogui.press('left')
    return jsonify({"status": "success", "message": "Moved to previous slide"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
