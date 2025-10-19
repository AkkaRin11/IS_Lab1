package org.example.ic_lab1.view;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import lombok.RequiredArgsConstructor;
import org.example.ic_lab1.dto.PersonDTO;
import org.example.ic_lab1.enm.Color;
import org.example.ic_lab1.enm.Country;
import org.example.ic_lab1.service.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Route("")
@CssImport("./styles/shared-styles.css")
@RequiredArgsConstructor
public class MainView extends VerticalLayout {

    private final PersonService personService;
    private final Grid<PersonDTO> grid = new Grid<>(PersonDTO.class, false);
    private List<PersonDTO> allPersons;
    private List<PersonDTO> filteredPersons;
    private int currentPage = 0;
    private final int pageSize = 9;
    private final Span pageInfo = new Span();
    private final Button prevButton = new Button("← Previous");
    private final Button nextButton = new Button("Next →");
    private final Button firstButton = new Button("First");
    private final Button lastButton = new Button("Last");
    private final TextField filterField = new TextField();
    private final Button clearFilterButton = new Button("Clear Filter");

    @PostConstruct
    public void init() {
        getElement().getStyle().set("height", "100vh");
        getElement().getStyle().set("margin", "0");
        getElement().getStyle().set("padding", "0");

        setupGridColumns();

        refreshGrid();

        setupFiltering();

        setupPagination();

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setPadding(true);
        contentLayout.setSpacing(true);
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
    }

    private void setupGridColumns() {
        grid.addColumn(PersonDTO::getId).setHeader("ID").setSortable(true).setAutoWidth(true);
        grid.addColumn(PersonDTO::getName).setHeader("Name").setSortable(true).setAutoWidth(true);
        grid.addColumn(PersonDTO::getCoordinatesX).setHeader("Coordinates X").setSortable(true).setFlexGrow(1);
        grid.addColumn(PersonDTO::getCoordinatesY).setHeader("Coordinates Y").setSortable(true).setFlexGrow(1);
        grid.addColumn(PersonDTO::getHeight).setHeader("Height").setSortable(true).setAutoWidth(true);
        grid.addColumn(PersonDTO::getEyeColor).setHeader("Eye Color").setSortable(true).setFlexGrow(2);
        grid.addColumn(PersonDTO::getHairColor).setHeader("Hair Color").setSortable(true).setFlexGrow(2);
        grid.addColumn(PersonDTO::getNationality).setHeader("Nationality").setSortable(true).setFlexGrow(2);
        grid.addColumn(PersonDTO::getLocationX).setHeader("Location X").setSortable(true).setAutoWidth(true);
        grid.addColumn(PersonDTO::getLocationY).setHeader("Location Y").setSortable(true).setAutoWidth(true);
        grid.addColumn(PersonDTO::getLocationName).setHeader("Location Name").setSortable(true).setFlexGrow(1);

        grid.addComponentColumn(this::createEditButton)
                .setHeader("Actions")
                .setAutoWidth(true)
                .setFlexGrow(0);


    }

    private Button createEditButton(PersonDTO person) {
        Button editButton = new Button(new Icon(VaadinIcon.EDIT));
        editButton.addClassName("edit-button");
        editButton.setTooltipText("Edit person");

        editButton.addClickListener(e -> openEditDialog(person));

        return editButton;
    }

    private void openEditDialog(PersonDTO person) {
        Dialog editDialog = new Dialog();
        editDialog.setHeaderTitle("Edit Person: " + person.getName());

        FormLayout formLayout = new FormLayout();

        // Name field (not null, not empty)
        TextField nameField = new TextField("Name *");
        nameField.setValue(person.getName() != null ? person.getName() : "");
        nameField.setRequired(true);
        nameField.setErrorMessage("Name cannot be empty");
        nameField.setRequiredIndicatorVisible(true);

        // Coordinates X (not null)
        NumberField coordinatesXField = new NumberField("Coordinates X *");
        coordinatesXField.setValue(person.getCoordinatesX());
        coordinatesXField.setRequired(true);
        coordinatesXField.setRequiredIndicatorVisible(true);

        // Coordinates Y (not null)
        NumberField coordinatesYField = new NumberField("Coordinates Y *");
        coordinatesYField.setValue(person.getCoordinatesY().doubleValue());
        coordinatesYField.setRequired(true);
        coordinatesYField.setRequiredIndicatorVisible(true);

        // Height (must be greater than 0)
        NumberField heightField = new NumberField("Height *");
        heightField.setValue((double) person.getHeight());
        heightField.setMin(0.1);
        heightField.setErrorMessage("Height must be greater than 0");
        heightField.setRequiredIndicatorVisible(true);

        // Eye Color (can be null)
        Select<Color> eyeColorField = new Select<>();
        eyeColorField.setLabel("Eye Color");
        eyeColorField.setItems(Arrays.asList(Color.values()));
        eyeColorField.setEmptySelectionAllowed(true);
        eyeColorField.setEmptySelectionCaption("Not specified");
        eyeColorField.setValue(person.getEyeColor());

        // Hair Color (can be null)
        Select<Color> hairColorField = new Select<>();
        hairColorField.setLabel("Hair Color");
        hairColorField.setItems(Arrays.asList(Color.values()));
        hairColorField.setEmptySelectionAllowed(true);
        hairColorField.setEmptySelectionCaption("Not specified");
        hairColorField.setValue(person.getHairColor());

        // Nationality (not null)
        Select<Country> nationalityField = new Select<>();
        nationalityField.setLabel("Nationality *");
        nationalityField.setItems(Arrays.asList(Country.values()));
        nationalityField.setValue(person.getNationality());
        nationalityField.setRequiredIndicatorVisible(true);

        // Location Name (can be null)
        TextField locationNameField = new TextField("Location Name");
        locationNameField.setValue(person.getLocationName() != null ? person.getLocationName() : "");

        // Location X (can be null)
        NumberField locationXField = new NumberField("Location X");
        locationXField.setValue(person.getLocationX() != null ? person.getLocationX() : 0.0);

        // Location Y (can be null)
        NumberField locationYField = new NumberField("Location Y");
        locationYField.setValue(person.getLocationY() != null ? person.getLocationY() : 0.0);

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
            if (validateForm(nameField, coordinatesXField, coordinatesYField,
                    heightField, nationalityField)) {
                savePersonChanges(
                        person,
                        nameField.getValue(),
                        coordinatesXField.getValue(),
                        coordinatesYField.getValue() != null ? coordinatesYField.getValue().floatValue() : null,
                        heightField.getValue() != null ? heightField.getValue().floatValue() : 0.0f,
                        eyeColorField.getValue(),
                        hairColorField.getValue(),
                        nationalityField.getValue(),
                        locationNameField.getValue(),
                        locationXField.getValue(),
                        locationYField.getValue()
                );
                editDialog.close();
                refreshGrid();
                Notification.show("Person updated successfully!");
            }
        });

        Button cancelButton = new Button("Cancel", event -> editDialog.close());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        editDialog.add(formLayout, buttonsLayout);

        editDialog.open();
    }

    private boolean validateForm(TextField nameField, NumberField coordinatesXField,
                                 NumberField coordinatesYField, NumberField heightField,
                                 Select<Country> nationalityField) {
        boolean isValid = true;

        // Validate name
        if (nameField.getValue() == null || nameField.getValue().trim().isEmpty()) {
            nameField.setInvalid(true);
            isValid = false;
        } else {
            nameField.setInvalid(false);
        }

        // Validate coordinates X
        if (coordinatesXField.getValue() == null) {
            coordinatesXField.setInvalid(true);
            isValid = false;
        } else {
            coordinatesXField.setInvalid(false);
        }

        // Validate coordinates Y
        if (coordinatesYField.getValue() == null) {
            coordinatesYField.setInvalid(true);
            isValid = false;
        } else {
            coordinatesYField.setInvalid(false);
        }

        // Validate height
        if (heightField.getValue() == null || heightField.getValue() <= 0) {
            heightField.setInvalid(true);
            isValid = false;
        } else {
            heightField.setInvalid(false);
        }

        // Validate nationality
        if (nationalityField.getValue() == null) {
            nationalityField.setInvalid(true);
            isValid = false;
        } else {
            nationalityField.setInvalid(false);
        }

        if (!isValid) {
            Notification.show("Please correct the errors in the form", 3000,
                    Notification.Position.MIDDLE);
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
            Notification.show("Error updating person: " + e.getMessage(), 5000,
                    Notification.Position.MIDDLE);
        }
    }

    private void setupFiltering() {
        filterField.setPlaceholder("Filter by any string column (exact match)...");
        filterField.setClearButtonVisible(true);
        filterField.setWidth("300px");

        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.setValueChangeTimeout(300);
        filterField.addValueChangeListener(e -> applyFilter());

        clearFilterButton.addClickListener(e -> {
            filterField.clear();
            applyFilter();
        });
    }

    private void applyFilter() {
        String filterText = filterField.getValue().trim();

        if (filterText.isEmpty()) {
            filteredPersons = allPersons;
        } else {
            filteredPersons = allPersons.stream()
                    .filter(person -> matchesFilter(person, filterText))
                    .collect(Collectors.toList());
        }

        currentPage = 0;
        updateGridPage();
    }

    private boolean matchesFilter(PersonDTO person, String filterText) {
        return (person.getName() != null && person.getName().equals(filterText)) ||
                (person.getEyeColor() != null && person.getEyeColor().toString().equals(filterText)) ||
                (person.getHairColor() != null && person.getHairColor().toString().equals(filterText)) ||
                (person.getNationality() != null && person.getNationality().toString().equals(filterText)) ||
                (person.getLocationName() != null && person.getLocationName().equals(filterText)) ||
                (person.getId() != null && person.getId().toString().equals(filterText)) ||
                (String.valueOf(person.getCoordinatesX()).equals(filterText)) ||
                (person.getCoordinatesY() != null && person.getCoordinatesY().toString().equals(filterText)) ||
                (String.valueOf(person.getHeight()).equals(filterText)) ||
                (person.getLocationX() != null && person.getLocationX().toString().equals(filterText)) ||
                (person.getLocationY() != null && person.getLocationY().toString().equals(filterText));
    }

    private void refreshGrid() {
        allPersons = personService.findAllPersons();
        filteredPersons = allPersons;
        updateGridPage();
    }

    private void setupPagination() {
        firstButton.addClickListener(e -> {
            currentPage = 0;
            updateGridPage();
        });

        prevButton.addClickListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateGridPage();
            }
        });

        nextButton.addClickListener(e -> {
            if ((currentPage + 1) * pageSize < filteredPersons.size()) {
                currentPage++;
                updateGridPage();
            }
        });

        lastButton.addClickListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredPersons.size() / pageSize);
            currentPage = Math.max(0, totalPages - 1);
            updateGridPage();
        });

        updatePaginationButtons();
    }

    private void updateGridPage() {
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredPersons.size());

        if (fromIndex < filteredPersons.size()) {
            List<PersonDTO> pageItems = filteredPersons.subList(fromIndex, toIndex);
            grid.setItems(pageItems);
        } else {
            grid.setItems();
        }

        updatePageInfo();
        updatePaginationButtons();
    }

    private void updatePageInfo() {
        int total = filteredPersons.size();
        int from = Math.min(currentPage * pageSize + 1, total);
        int to = Math.min((currentPage + 1) * pageSize, total);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        pageInfo.setText(String.format("Page %d of %d | Showing %d-%d of %d | Total filtered: %d",
                currentPage + 1, Math.max(1, totalPages), from, to, total, total));
    }

    private void updatePaginationButtons() {
        int totalPages = (int) Math.ceil((double) filteredPersons.size() / pageSize);

        firstButton.setEnabled(currentPage > 0);
        prevButton.setEnabled(currentPage > 0);
        nextButton.setEnabled(currentPage < totalPages - 1);
        lastButton.setEnabled(currentPage < totalPages - 1);
    }

    private HorizontalLayout createFilterLayout() {
        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setAlignItems(Alignment.CENTER);
        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        filterLayout.add(filterField, clearFilterButton);
        return filterLayout;
    }

    private HorizontalLayout createPaginationLayout() {
        HorizontalLayout paginationLayout = new HorizontalLayout();
        paginationLayout.setAlignItems(Alignment.CENTER);
        paginationLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        paginationLayout.add(firstButton, prevButton, pageInfo, nextButton, lastButton);
        return paginationLayout;
    }
}