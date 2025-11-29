package org.example.ic_lab1.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.PollEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.dto.CoordinatesDTO;
import org.example.ic_lab1.dto.LocationDTO;
import org.example.ic_lab1.dto.PersonDTO;
import org.example.ic_lab1.enm.Color;
import org.example.ic_lab1.enm.Country;
import org.example.ic_lab1.service.PersonService;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Route("")
@CssImport("./styles/shared-styles.css")
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final PersonService personService;
    private final Grid<PersonDTO> grid = new Grid<>(PersonDTO.class, false);
    private final int pageSize = 9;
    private final TextField filterField = new TextField();
    private final Button clearFilterButton = new Button("Clear Filter");

    private int currentPage = 0;
    private String currentSortProperty = "id";
    private boolean currentSortAscending = true;
    private long totalSize = 0;


    @PostConstruct
    public void init() {
        setupGridColumns();

        Button addButton = new Button(new Icon(VaadinIcon.PLUS));
        addButton.setTooltipText("Добавить нового человека");
        addButton.addClickListener(e -> openAddDialog());

        grid.setItems(query -> {
            int ignoredOffset = query.getOffset();
            int ignoredLimit = query.getLimit();

            int offset = currentPage * pageSize;
            int limit = pageSize;

            String filterText = filterField.getValue();

            if (!query.getSortOrders().isEmpty()) {
                QuerySortOrder sort = query.getSortOrders().get(0);
                currentSortProperty = sort.getSorted();
                currentSortAscending = sort.getDirection() == SortDirection.ASCENDING;
            }

            var result = personService.findPersons(
                    filterText,
                    currentSortProperty,
                    currentSortAscending,
                    offset,
                    limit
            );

            totalSize = personService.countPersons(filterText);
            return result.stream();
        });


        filterField.setPlaceholder("Filter...");
        filterField.setClearButtonVisible(true);
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.addValueChangeListener(e -> {
            currentPage = 0;
            grid.getDataProvider().refreshAll();
        });

        clearFilterButton.addClickListener(e -> {
            filterField.clear();
            currentPage = 0;
            grid.getDataProvider().refreshAll();
        });

        VerticalLayout contentLayout = new VerticalLayout();

        contentLayout.setPadding(true);
        contentLayout.setSpacing(true);
        contentLayout.setWidthFull();
        contentLayout.add(createFilterLayout(), grid, createPaginationLayout());

        Div contentContainer = new Div(contentLayout);
        contentContainer.addClassName("content-container");

        Div backgroundContainer = new Div(contentContainer);
        backgroundContainer.addClassName("background-container");

        add(backgroundContainer);

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        setMargin(false);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        UI.getCurrent().setPollInterval(5000);
        UI.getCurrent().addPollListener(this::refreshGrid);
    }

    private void refreshGrid(PollEvent event) {
        grid.getDataProvider().refreshAll();
        grid.getDataCommunicator().reset();
    }

    private void setupGridColumns() {
        grid.addColumn(PersonDTO::getId)
                .setHeader("ID")
                .setSortable(true)
                .setSortProperty("id")
                .setAutoWidth(true);

        grid.addColumn(PersonDTO::getName)
                .setHeader("Name")
                .setSortable(true)
                .setSortProperty("name")
                .setAutoWidth(true);

        grid.addColumn(PersonDTO::getCoordinatesX)
                .setHeader("Coordinates X")
                .setSortable(true)
                .setSortProperty("coordinatesX")
                .setFlexGrow(1);

        grid.addColumn(PersonDTO::getCoordinatesY)
                .setHeader("Coordinates Y")
                .setSortable(true)
                .setSortProperty("coordinatesY")
                .setFlexGrow(1);

        grid.addColumn(PersonDTO::getHeight)
                .setHeader("Height")
                .setSortable(true)
                .setSortProperty("height")
                .setAutoWidth(true);

        grid.addColumn(PersonDTO::getEyeColor)
                .setHeader("Eye Color")
                .setSortable(true)
                .setSortProperty("eyeColor")
                .setFlexGrow(2);

        grid.addColumn(PersonDTO::getHairColor)
                .setHeader("Hair Color")
                .setSortable(true)
                .setSortProperty("hairColor")
                .setFlexGrow(2);

        grid.addColumn(PersonDTO::getNationality)
                .setHeader("Nationality")
                .setSortable(true)
                .setSortProperty("nationality")
                .setFlexGrow(2);

        grid.addColumn(PersonDTO::getLocationX)
                .setHeader("Location X")
                .setSortable(true)
                .setSortProperty("locationX")
                .setAutoWidth(true);

        grid.addColumn(PersonDTO::getLocationY)
                .setHeader("Location Y")
                .setSortable(true)
                .setSortProperty("locationY")
                .setAutoWidth(true);

        grid.addColumn(PersonDTO::getLocationName)
                .setHeader("Location Name")
                .setSortable(true)
                .setSortProperty("locationName")
                .setFlexGrow(1);

        grid.addComponentColumn(person -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addClassName("edit-button");
            editButton.setTooltipText("Редактировать");
            editButton.addClickListener(e -> openEditDialog(person));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addClassName("delete-button");
            deleteButton.setTooltipText("Удалить");
            deleteButton.addClickListener(e -> deletePerson(person));

            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Действия").setAutoWidth(true).setFlexGrow(0);
    }

    private void deletePerson(PersonDTO person) {
        try {
            personService.deletePerson(person.getId());
            grid.getDataProvider().refreshAll();
            Notification.show("Удалено!");
        } catch (Exception e) {
            Notification.show("Ошибка при удалении: " + e.getMessage());
        }
    }

    private void openAddDialog() {
        Dialog addDialog = new Dialog();
        addDialog.setHeaderTitle("Добавить нового человека");

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Name *");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setHelperText("Name cannot be empty");

        NumberField coordinatesXField = new NumberField("Coordinates X *");
        coordinatesXField.setRequiredIndicatorVisible(true);
        coordinatesXField.setHelperText("X must be greater than -620");
        coordinatesXField.setMin(-619.999);

        NumberField coordinatesYField = new NumberField("Coordinates Y *");
        coordinatesYField.setRequiredIndicatorVisible(true);
        coordinatesYField.setHelperText("Y cannot be null and must be <= 647");
        coordinatesYField.setMax(647.0);

        NumberField heightField = new NumberField("Height *");
        heightField.setRequiredIndicatorVisible(true);
        heightField.setHelperText("Height must be greater than 0");
        heightField.setMin(0.1);

        Select<Color> eyeColorField = new Select<>();
        eyeColorField.setLabel("Eye Color");
        eyeColorField.setItems(Arrays.asList(Color.values()));
        eyeColorField.setEmptySelectionAllowed(true);
        eyeColorField.setEmptySelectionCaption("Not specified");

        Select<Color> hairColorField = new Select<>();
        hairColorField.setLabel("Hair Color");
        hairColorField.setItems(Arrays.asList(Color.values()));
        hairColorField.setEmptySelectionAllowed(true);
        hairColorField.setEmptySelectionCaption("Not specified");

        Select<Country> nationalityField = new Select<>();
        nationalityField.setLabel("Nationality *");
        nationalityField.setItems(Arrays.asList(Country.values()));
        nationalityField.setRequiredIndicatorVisible(true);

        TextField locationNameField = new TextField("Location Name *");
        locationNameField.setRequiredIndicatorVisible(true);
        locationNameField.setHelperText("Location name cannot be empty");

        NumberField locationXField = new NumberField("Location X *");
        locationXField.setRequiredIndicatorVisible(true);
        locationXField.setHelperText("Location X cannot be null");

        NumberField locationYField = new NumberField("Location Y *");
        locationYField.setRequiredIndicatorVisible(true);
        locationYField.setHelperText("Location Y cannot be null");

        formLayout.add(
                nameField,
                coordinatesXField,
                coordinatesYField,
                heightField,
                eyeColorField,
                hairColorField,
                nationalityField,
                locationNameField,
                locationXField,
                locationYField
        );

        Button saveButton = new Button("Save", event -> {
            boolean valid = validatePersonForm(
                    nameField,
                    coordinatesXField,
                    coordinatesYField,
                    heightField,
                    nationalityField,
                    locationNameField,
                    locationXField,
                    locationYField
            );

            if (!valid) return;

            try {
                PersonDTO newPerson = new PersonDTO();

                CoordinatesDTO coords = new CoordinatesDTO();
                coords.setX(coordinatesXField.getValue());
                coords.setY(coordinatesYField.getValue() != null ? coordinatesYField.getValue().floatValue() : null);
                newPerson.setCoordinates(coords);

                LocationDTO location = new LocationDTO();
                location.setName(locationNameField.getValue());
                location.setX(locationXField.getValue());
                location.setY(locationYField.getValue());
                newPerson.setLocation(location);

                newPerson.setName(nameField.getValue());
                newPerson.setHeight(heightField.getValue() != null ? heightField.getValue().floatValue() : 0.0f);
                newPerson.setEyeColor(eyeColorField.getValue());
                newPerson.setHairColor(hairColorField.getValue());
                newPerson.setNationality(nationalityField.getValue());

                personService.createPerson(newPerson);
                grid.getDataProvider().refreshAll();
                Notification.show("Person added successfully!");
                addDialog.close();
            } catch (Exception e) {
                Notification.show("Error adding person: " + e.toString(), 5000, Notification.Position.MIDDLE);
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel", event -> addDialog.close());
        addDialog.add(formLayout, new HorizontalLayout(saveButton, cancelButton));
        addDialog.open();
    }

    private Button createEditButton(PersonDTO person) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addClassName("edit-button");
        editButton.setTooltipText("Edit person");
        editButton.addClickListener(e -> openEditDialog(person));
        return editButton;
    }

    private void openEditDialog(PersonDTO person) {
        if (person.getCoordinates() == null) person.setCoordinates(new CoordinatesDTO());
        if (person.getLocation() == null) person.setLocation(new LocationDTO());

        Dialog editDialog = new Dialog();
        editDialog.setHeaderTitle("Edit Person: " + person.getName());

        FormLayout formLayout = new FormLayout();

        TextField nameField = new TextField("Name *");
        nameField.setValue(person.getName() != null ? person.getName() : "");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setHelperText("Name cannot be empty");

        NumberField coordinatesXField = new NumberField("Coordinates X *");
        coordinatesXField.setValue(person.getCoordinates().getX());
        coordinatesXField.setRequiredIndicatorVisible(true);
        coordinatesXField.setHelperText("X must be greater than -620");
        coordinatesXField.setMin(-619.999);

        NumberField coordinatesYField = new NumberField("Coordinates Y *");
        coordinatesYField.setValue(person.getCoordinates().getY() != null ? person.getCoordinates().getY().doubleValue() : null);
        coordinatesYField.setRequiredIndicatorVisible(true);
        coordinatesYField.setHelperText("Y cannot be null and must be <= 647");
        coordinatesYField.setMax(647.0);

        NumberField heightField = new NumberField("Height *");
        heightField.setValue((double) person.getHeight());
        heightField.setMin(0.1);
        heightField.setRequiredIndicatorVisible(true);
        heightField.setHelperText("Height must be greater than 0");

        Select<Color> eyeColorField = new Select<>();
        eyeColorField.setLabel("Eye Color");
        eyeColorField.setItems(Arrays.asList(Color.values()));
        eyeColorField.setEmptySelectionAllowed(true);
        eyeColorField.setEmptySelectionCaption("Not specified");
        eyeColorField.setValue(person.getEyeColor());

        Select<Color> hairColorField = new Select<>();
        hairColorField.setLabel("Hair Color");
        hairColorField.setItems(Arrays.asList(Color.values()));
        hairColorField.setEmptySelectionAllowed(true);
        hairColorField.setEmptySelectionCaption("Not specified");
        hairColorField.setValue(person.getHairColor());

        Select<Country> nationalityField = new Select<>();
        nationalityField.setLabel("Nationality *");
        nationalityField.setItems(Arrays.asList(Country.values()));
        nationalityField.setRequiredIndicatorVisible(true);
        nationalityField.setValue(person.getNationality());

        TextField locationNameField = new TextField("Location Name *");
        locationNameField.setValue(person.getLocation().getName() != null ? person.getLocation().getName() : "");
        locationNameField.setRequiredIndicatorVisible(true);
        locationNameField.setHelperText("Location name cannot be empty");

        NumberField locationXField = new NumberField("Location X *");
        locationXField.setValue(person.getLocation().getX() != null ? person.getLocation().getX() : 0.0);
        locationXField.setRequiredIndicatorVisible(true);
        locationXField.setHelperText("Location X cannot be null");

        NumberField locationYField = new NumberField("Location Y *");
        locationYField.setValue(person.getLocation().getY() != null ? person.getLocation().getY() : 0.0);
        locationYField.setRequiredIndicatorVisible(true);
        locationYField.setHelperText("Location Y cannot be null");

        formLayout.add(
                nameField, coordinatesXField, coordinatesYField, heightField,
                eyeColorField, hairColorField, nationalityField,
                locationNameField, locationXField, locationYField
        );

        Button saveButton = new Button("Save", event -> {
            if (validatePersonForm(nameField, coordinatesXField, coordinatesYField, heightField,
                    nationalityField, locationNameField, locationXField, locationYField)) {

                person.getCoordinates().setX(coordinatesXField.getValue());
                person.getCoordinates().setY(coordinatesYField.getValue().floatValue());
                person.setHeight(heightField.getValue().floatValue());
                person.setName(nameField.getValue());
                person.setEyeColor(eyeColorField.getValue());
                person.setHairColor(hairColorField.getValue());
                person.setNationality(nationalityField.getValue());
                person.getLocation().setName(locationNameField.getValue());
                person.getLocation().setX(locationXField.getValue());
                person.getLocation().setY(locationYField.getValue());

                personService.updatePerson(person);
                editDialog.close();
                grid.getDataProvider().refreshAll();
                Notification.show("Person updated successfully!");
            }
        });

        Button cancelButton = new Button("Cancel", event -> editDialog.close());
        editDialog.add(formLayout, new HorizontalLayout(saveButton, cancelButton));
        editDialog.open();
    }

    private boolean validatePersonForm(TextField nameField,
                                       NumberField coordinatesXField,
                                       NumberField coordinatesYField,
                                       NumberField heightField,
                                       Select<Country> nationalityField,
                                       TextField locationNameField,
                                       NumberField locationXField,
                                       NumberField locationYField) {
        boolean isValid = true;

        if (nameField.getValue() == null || nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true); isValid = false;
        } else nameField.setInvalid(false);

        if (coordinatesXField.getValue() == null || coordinatesXField.getValue() <= -620) {
            coordinatesXField.setInvalid(true); isValid = false;
        } else coordinatesXField.setInvalid(false);

        if (coordinatesYField.getValue() == null || coordinatesYField.getValue() > 647) {
            coordinatesYField.setInvalid(true); isValid = false;
        } else coordinatesYField.setInvalid(false);

        if (heightField.getValue() == null || heightField.getValue() <= 0) {
            heightField.setInvalid(true); isValid = false;
        } else heightField.setInvalid(false);

        if (nationalityField.getValue() == null) {
            nationalityField.setInvalid(true); isValid = false;
        } else nationalityField.setInvalid(false);

        if (locationNameField.getValue() == null || locationNameField.getValue().trim().isEmpty()) {
            locationNameField.setInvalid(true); isValid = false;
        } else locationNameField.setInvalid(false);

        if (locationXField.getValue() == null) {
            locationXField.setInvalid(true); isValid = false;
        } else locationXField.setInvalid(false);

        if (locationYField.getValue() == null) {
            locationYField.setInvalid(true); isValid = false;
        } else locationYField.setInvalid(false);

        if (!isValid) {
            Notification.show("Please correct the errors in the form", 3000, Notification.Position.MIDDLE);
        }

        return isValid;
    }

    private boolean validateForm(TextField nameField, NumberField coordinatesXField,
                                 NumberField coordinatesYField, NumberField heightField,
                                 Select<Country> nationalityField) {
        boolean isValid = true;

        if (nameField.getValue() == null || nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true);
            isValid = false;
        } else nameField.setInvalid(false);

        if (coordinatesXField.getValue() == null) {
            coordinatesXField.setInvalid(true);
            isValid = false;
        } else coordinatesXField.setInvalid(false);

        if (coordinatesYField.getValue() == null) {
            coordinatesYField.setInvalid(true);
            isValid = false;
        } else coordinatesYField.setInvalid(false);

        if (heightField.getValue() == null || heightField.getValue() <= 0) {
            heightField.setInvalid(true);
            isValid = false;
        } else heightField.setInvalid(false);

        if (nationalityField.getValue() == null) {
            nationalityField.setInvalid(true);
            isValid = false;
        } else nationalityField.setInvalid(false);

        if (!isValid) {
            Notification.show("Please correct the errors in the form", 3000, Notification.Position.MIDDLE);
        }

        return isValid;
    }

    private void savePersonChanges(
            PersonDTO person,
            String name,
            Double coordinatesX,
            Float coordinatesY,
            Float height,
            Color eyeColor,
            Color hairColor,
            Country nationality,
            String locationName,
            Double locationX,
            Double locationY
    ) {
        try {
            person.setName(name);
            person.setCoordinatesX(coordinatesX);
            person.setCoordinatesY(coordinatesY);
            person.setHeight(height);
            person.setEyeColor(eyeColor);
            person.setHairColor(hairColor);
            person.setNationality(nationality);
            person.setLocationName(locationName);
            person.setLocationX(locationX);
            person.setLocationY(locationY);

            personService.updatePerson(person);
        } catch (Exception e) {
            Notification.show("Error updating person: " + e.getMessage(), 5000, Notification.Position.MIDDLE);
        }
    }

    private HorizontalLayout createFilterLayout() {
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setAlignItems(Alignment.CENTER);
        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Button addButton = new Button(new Icon(VaadinIcon.PLUS));
        addButton.setTooltipText("Add new person");
        addButton.addClickListener(e -> openAddDialog());

        filterLayout.add(filterField, clearFilterButton, addButton);
        return filterLayout;
    }

    private HorizontalLayout createPaginationLayout() {
        Button first = new Button("First");
        Button prev = new Button("Prev");
        Button next = new Button("Next");
        Button last = new Button("Last");

        TextField pageInfo = new TextField();
        pageInfo.setReadOnly(true);
        pageInfo.setWidth("100px");

        Runnable updatePageInfo = () -> {
            long totalPages = (totalSize + pageSize - 1) / pageSize;
            if (totalPages == 0) totalPages = 1;
            pageInfo.setValue((currentPage + 1) + " / " + totalPages);
        };

        first.addClickListener(e -> {
            currentPage = 0;
            grid.getDataProvider().refreshAll();
            updatePageInfo.run();
        });

        prev.addClickListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                grid.getDataProvider().refreshAll();
                updatePageInfo.run();
            }
        });

        next.addClickListener(e -> {
            if ((currentPage + 1) * pageSize < totalSize) {
                currentPage++;
                grid.getDataProvider().refreshAll();
                updatePageInfo.run();
            }
        });

        last.addClickListener(e -> {
            currentPage = (int) ((totalSize - 1) / pageSize);
            grid.getDataProvider().refreshAll();
            updatePageInfo.run();
        });

        grid.getDataProvider().addDataProviderListener(e -> updatePageInfo.run());

        HorizontalLayout layout = new HorizontalLayout(first, prev, pageInfo, next, last);
        layout.setAlignItems(Alignment.CENTER);

        Button deleteByHeightButton = new Button("Delete by height");
        Button sumHeightButton = new Button("The amount of height");
        Button groupByNationalityButton = new Button("Group by nationality");
        Button hairColorShareButton = new Button("Proportion by hair color");
        Button countByHairAndLocationButton = new Button("Quantity by color and location");

        deleteByHeightButton.addClickListener(e -> openDeleteByHeightDialog());
        sumHeightButton.addClickListener(e -> showSumHeight());
        groupByNationalityButton.addClickListener(e -> showGroupByNationality());
        hairColorShareButton.addClickListener(e -> openHairColorShareDialog());
        countByHairAndLocationButton.addClickListener(e -> openCountByHairAndLocationDialog());


        deleteByHeightButton.addClassName("action-button");
        sumHeightButton.addClassName("action-button");
        groupByNationalityButton.addClassName("action-button");
        hairColorShareButton.addClassName("action-button");
        countByHairAndLocationButton.addClassName("action-button");

        HorizontalLayout rightActions = new HorizontalLayout(
                deleteByHeightButton,
                sumHeightButton,
                groupByNationalityButton,
                hairColorShareButton,
                countByHairAndLocationButton
        );
        rightActions.addClassName("right-actions");

        HorizontalLayout fullLayout = new HorizontalLayout(layout, rightActions);
        fullLayout.setWidthFull();
        fullLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        fullLayout.setAlignItems(Alignment.CENTER);

        return fullLayout;
    }

    public void openDeleteByHeightDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Delete by Height");

        NumberField heightField = new NumberField("Height *");
        heightField.setRequiredIndicatorVisible(true);
        heightField.setMin(0.1);
        heightField.setHelperText("Enter height value to delete all matching persons");

        Button deleteButton = new Button("Delete", e -> {
            if (heightField.getValue() == null || heightField.getValue() <= 0) {
                heightField.setInvalid(true);
                Notification.show("Height must be a positive number", 3000, Notification.Position.MIDDLE);
                return;
            }
            heightField.setInvalid(false);
            try {
                personService.deleteAllByHeight(heightField.getValue().floatValue());
                grid.getDataProvider().refreshAll();
                Notification.show("Persons deleted successfully!");
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        });

        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(heightField, new HorizontalLayout(deleteButton, cancel));
        dialog.open();
    }

    public void showSumHeight() {
        try {
            Float sum = personService.sumHeight();
            Notification.show("Total height sum: " + (sum != null ? String.format("%.2f", sum) : "0"));
        } catch (Exception e) {
            Notification.show("Error calculating sum: " + e.getMessage());
        }
    }

    public void showGroupByNationality() {
        try {
            Map<Country, Long> groups = personService.groupByNationality();
            if (groups.isEmpty()) {
                Notification.show("No persons to group");
                return;
            }

            StringBuilder sb = new StringBuilder("Group by nationality:\n");
            groups.forEach((country, count) ->
                    sb.append(country != null ? country : "Unknown").append(": ").append(count).append("\n")
            );

            Notification notification = new Notification(sb.toString(), 3000, Notification.Position.MIDDLE);
            notification.addClassName("long-notification");
            notification.open();
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
        }
    }

    public void openHairColorShareDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Hair Color Share");

        Select<Color> hairColorSelect = new Select<>();
        hairColorSelect.setLabel("Hair Color *");
        hairColorSelect.setItems(Arrays.asList(Color.values()));
        hairColorSelect.setRequiredIndicatorVisible(true);

        Button calculateButton = new Button("Calculate", e -> {
            if (hairColorSelect.getValue() == null) {
                hairColorSelect.setInvalid(true);
                Notification.show("Please select a hair color", 3000, Notification.Position.MIDDLE);
                return;
            }
            hairColorSelect.setInvalid(false);
            try {
                Float share = personService.getHairColorShare(hairColorSelect.getValue());
                Notification.show(String.format("Share of %s hair color: %.2f%%",
                        hairColorSelect.getValue(), share));
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });

        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(hairColorSelect, new HorizontalLayout(calculateButton, cancel));
        dialog.open();
    }

    public void openCountByHairAndLocationDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Count by Hair Color and Location");

        Select<Color> hairColorSelect = new Select<>();
        hairColorSelect.setLabel("Hair Color *");
        hairColorSelect.setItems(Arrays.asList(Color.values()));
        hairColorSelect.setRequiredIndicatorVisible(true);

        TextField locationNameField = new TextField("Location Name *");
        locationNameField.setRequiredIndicatorVisible(true);
        locationNameField.setHelperText("Exact location name");

        Button calculateButton = new Button("Calculate", e -> {
            boolean valid = true;
            if (hairColorSelect.getValue() == null) {
                hairColorSelect.setInvalid(true); valid = false;
            } else hairColorSelect.setInvalid(false);

            if (locationNameField.getValue() == null || locationNameField.getValue().trim().isEmpty()) {
                locationNameField.setInvalid(true); valid = false;
            } else locationNameField.setInvalid(false);

            if (!valid) {
                Notification.show("Please fill all fields");
                return;
            }

            try {
                Integer count = personService.countByHairColorAndLocation(
                        hairColorSelect.getValue(),
                        locationNameField.getValue().trim()
                );
                Notification.show(String.format("Found %d person(s) with %s hair in '%s'",
                        count, hairColorSelect.getValue(), locationNameField.getValue().trim()));
                dialog.close();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage());
            }
        });

        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.add(hairColorSelect, locationNameField, new HorizontalLayout(calculateButton, cancel));
        dialog.open();
    }
}