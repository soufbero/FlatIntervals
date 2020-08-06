package com.souf.ui;

import com.souf.dto.IntervalDto;
import com.souf.flatten.RangeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

@Route("flatten")
@PageTitle("Flatten Ranges Test")
@Theme(Material.class)
public class MainUI extends VerticalLayout {

    private RangeService rangeService = new RangeService();

    public MainUI(){
        Label header = new Label("Flatten Range Tester");
        TextArea ranges = new TextArea("Enter the ranges in format min,max with each range in a different line:",
                "");
        TextArea results = new TextArea("Resulting Set of Ranges Step by Step:",
                "");
        Button generateButton = new Button("Generate", VaadinIcon.ARROW_RIGHT.create());

        header.getElement().getStyle().set("color", "red");
        header.getElement().getStyle().set("fontWeight", "bold");
        header.getElement().getStyle().set("font-size", "45px");
        header.getElement().getStyle().set("font-family", "Helvetica");
        setHorizontalComponentAlignment(Alignment.CENTER,header);
        add(header);
        ranges.setSizeFull();
        addAndExpand(ranges);
        add(generateButton);
        results.setEnabled(false);
        results.setSizeFull();
        addAndExpand(results);

        generateButton.addClickListener(click -> {
            results.setValue("");
            String input = ranges.getValue();
            List<IntervalDto> originalRanges = rangeService.generateRangesFromString(input);
            rangeService.generateRangeSetFromOriginalRanges(originalRanges);
            String output = rangeService.formatRangeSet();
            results.setValue(output);
        });

    }
}
