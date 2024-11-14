from kivy.app import App
from kivy.uix.switch import Switch
from kivy.uix.label import Label
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.dropdown import DropDown
from kivy.uix.popup import Popup
from kivy.uix.slider import Slider
from kivy.graphics import Ellipse, Color
from kivy.uix.image import Image
from kivy.core.window import Window


Window.clearcolor = (0.95, 0.95, 0.95, 1)  # background color to light gray

class Vibrate(App):
    def build(self):
        # main layout using BoxLayout for vertical stacking of widgets
        layout = BoxLayout(orientation='vertical', padding=20, spacing=15)

        # label that will display the current state of switches
        self.status_label = Label(text="Select options:", size_hint=(None, None), size=(250, 50), font_size=18, color=(0, 0, 0, 1))

        # horizontal layout for the "Start" button and the microphone icon
        top_button_layout = BoxLayout(orientation='horizontal', size_hint_y=None, height=120, spacing=20)

        # circular "Start" button with smooth rounded corners and better design
        self.start_button = Button(
            text="Start",
            font_size=24,
            size_hint=(None, None),
            size=(150, 150), 
            pos_hint={'center_x': 0.5},  # Center horizontally in layout
            background_normal='',  # Remove default background
            background_color=(1, 0, 0, 1),  # Red background (RGBA)
            border=(10, 10, 10, 10),  # Round corners
            color=(1, 1, 1, 1)  # White text color
        )

        # Bind the button press event to a method
        self.start_button.bind(on_press=self.on_button_press)

        # Draw the circular shape on the canvas
        with self.start_button.canvas.before:
            Color(1, 0, 0, 1)  # Red color (RGBA) by default
            self.circle = Ellipse(size=self.start_button.size, pos=self.start_button.pos)

        # Update the circle position and size when the button is resized or repositioned
        self.start_button.bind(pos=self.update_circle, size=self.update_circle)

        # Initialize a flag to track the button state (Start / Running)
        self.is_started = False  # Initially, the button is in "Start" state

        # Create the microphone icon to appear next to the button
        self.microphone_icon = Image(
            source= r'path',  # Use raw string for the path
            size_hint=(None, None),
            size=(50, 50)  # Adjust the size as necessary
        )
        
        # Add the button and the microphone icon to the horizontal layout
        top_button_layout.add_widget(self.start_button)
        top_button_layout.add_widget(self.microphone_icon)

        # three switches
        self.switch1 = Switch(active=False)
        self.switch2 = Switch(active=False)
        self.switch3 = Switch(active=False)

        # Bind the switch state to a function that updates the label
        self.switch1.bind(active=self.on_switch_change)
        self.switch2.bind(active=self.on_switch_change)
        self.switch3.bind(active=self.on_switch_change)

        # menu button (top right corner)
        menu_button = Button(
            text="☰", 
            size_hint=(None, None), 
            size=(50, 50), 
            background_color=(0.2, 0.6, 0.2, 1), 
            color=(1, 1, 1, 1)
        )
        menu_button.bind(on_press=self.open_menu)

        #DropDown for the menu
        self.dropdown = DropDown()

        settings_option = Button(text='Settings', size_hint_y=None, height=44, color=(0, 0, 0, 1))
        settings_option.bind(on_press=self.on_settings)
        self.dropdown.add_widget(settings_option)

        help_option = Button(text='Help', size_hint_y=None, height=44, color=(0, 0, 0, 1))
        help_option.bind(on_press=self.on_help)
        self.dropdown.add_widget(help_option)

        # Open the menu when the menu button is pressed
        menu_button.bind(on_release=self.dropdown.open)

        # layout for the top bar with the menu button
        top_layout = BoxLayout(size_hint_y=None, height=50, orientation='horizontal')
        top_layout.add_widget(menu_button)
        
        # widgets to the main layout
        layout.add_widget(top_layout)
        layout.add_widget(self.status_label)
        layout.add_widget(top_button_layout)  # "Start" button and microphone icon
        layout.add_widget(Label(text="Train", font_size=20, color=(0, 0, 0, 1)))
        layout.add_widget(self.switch1)
        layout.add_widget(Label(text="Car", font_size=20, color=(0, 0, 0, 1)))
        layout.add_widget(self.switch2)
        layout.add_widget(Label(text="Bicycle", font_size=20, color=(0, 0, 0, 1)))
        layout.add_widget(self.switch3)

        return layout

    def on_button_press(self, instance):
        # Toggle the button state between "Start" and "Running"
        if self.is_started:
            # Revert to "Start" state (red button)
            instance.background_color = (1, 0, 0, 1)  # Red (RGBA)
            instance.text = "Start"
            self.is_started = False  # Update the state to "Start"
        else:
            # Change to "Running" state (light green button)
            instance.background_color = (0.4, 1.0, 0.4, 1)  # Light green (RGBA)
            instance.text = "Running"
            self.is_started = True  # Update the state to "Running"

    def on_switch_change(self, instance, value):
        # Check which switches are turned on and update the label
        selected_options = []
        if self.switch1.active:
            selected_options.append("Train")
        if self.switch2.active:
            selected_options.append("Car")
        if self.switch3.active:
            selected_options.append("Bicycle")
        if selected_options:
            self.status_label.text = f"Selected: {', '.join(selected_options)}"
        else:
            self.status_label.text = "None selected."

    def update_circle(self, instance, value):
        # Update the position and size of the circle based on the button's properties
        self.circle.pos = instance.pos
        self.circle.size = instance.size

    def open_menu(self, instance):
        # Function to open the menu
        print("Menu button pressed")

    def on_settings(self, instance):
        # Action for Settings
        print("Settings selected")
        # Show a simple popup for settings
        content = BoxLayout(orientation='vertical', padding=15, spacing=10)

        # Label for intensity
        self.intensity_label = Label(text="Intensity: 50", font_size=16, color=(0, 0, 0, 1))
        
        # Slider for adjusting intensity (range from 0 to 100)
        self.intensity_slider = Slider(min=0, max=100, value=50)
        self.intensity_slider.bind(value=self.on_slider_value_change)

        # Label for frequency
        self.frequency_label = Label(text="Frequency: 2", font_size=16, color=(0, 0, 0, 1))
        
        # Slider for adjusting frequency (range from 0 to 5)
        self.frequency_slider = Slider(min=0, max=5, value=2)
        self.frequency_slider.bind(value=self.on_slider2_value_change)

        # Add sliders and labels to the settings popup
        content.add_widget(self.intensity_label)
        content.add_widget(self.intensity_slider)
        content.add_widget(self.frequency_label)
        content.add_widget(self.frequency_slider)

        # Close button to close the settings popup
        close_button = Button(text="Close", background_color=(0.2, 0.6, 0.2, 1), color=(1, 1, 1, 1))
        close_button.bind(on_press=self.close_popup)
        content.add_widget(close_button)

        self.popup = Popup(title="Settings", content=content, size_hint=(None, None), size=(400, 400))
        self.popup.open()

    def on_slider_value_change(self, slider, value):
        # Update the intensity label when the slider value changes
        self.intensity_label.text = f"Intensity: {int(value)}"
        
    def on_slider2_value_change(self, slider, value):
        # Update the frequency label when the slider value changes
        self.frequency_label.text = f"Frequency: {int(value)}"

    def on_help(self, instance):
        print("Help selected")
        content = BoxLayout(orientation='vertical', padding=15)
        content.add_widget(Label(text="Help information ", font_size=16, color=(0, 0, 0, 1)))
        close_button = Button(text="Close", background_color=(0.2, 0.6, 0.2, 1), color=(1, 1, 1, 1))
        close_button.bind(on_press=self.close_popup)
        content.add_widget(close_button)

        self.popup = Popup(title="Help", content=content, size_hint=(None, None), size=(400, 300))
        self.popup.open()

    def close_popup(self, instance):
        # Close the current popup
        self.popup.dismiss()


if __name__ == '__main__':
    Vibrate().run()
