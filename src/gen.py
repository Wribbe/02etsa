import sys

def gen_dialog_buttons():

    labels = ["Name", "SSN", "Address", "Phone", "Email"]
    for index, label in enumerate(labels):

        label_name = "label_{}".format(label.lower())
        field_name = "field_{}".format(label.lower())

        print("Label {} = new Label(\"{}\");".format(label_name, label))
        print("TextField {} = new TextField();".format(field_name))
        print("grid.add({}, 0, {});".format(label_name, index))
        print("grid.add({}, 1, {});".format(field_name, index))
        print("")

def gen_main_buttons():

    names = [
            "New user",
            "Edit user",
            "Remove user",
            "Add bike",
            "Remove bike",
            "Print barcode",
            ]

    def format_name(string):
        return string.replace(" ","_").lower()

    for index, name in enumerate(names, start=1):

        name_button = "button_{}".format(format_name(name))
        print("Button {} = new Button(\"{}\");".format(name_button,
                                                   name))
        print("grid.add({}, 1, {});".format(name_button, index))
        print("")


def main(args):

    if "popup" in args:
        gen_dialog_buttons()
    elif "main" in args:
        gen_main_buttons()


if __name__ == "__main__":
    main(sys.argv[1:])
