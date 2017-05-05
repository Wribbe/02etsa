def main():

    labels = ["Name", "SSN", "Address", "Phone", "Email"]
    for index, label in enumerate(labels):

        label_name = "label_{}".format(label.lower())
        field_name = "field_{}".format(label.lower())

        print("Label {} = new Label(\"{}\");".format(label_name, label))
        print("TextField {} = new TextField();".format(field_name))
        print("grid.add({}, 0, {});".format(label_name, index))
        print("grid.add({}, 1, {});".format(field_name, index))
        print("")

if __name__ == "__main__":
    main()
