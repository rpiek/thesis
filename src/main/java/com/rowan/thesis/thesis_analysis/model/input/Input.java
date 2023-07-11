package com.rowan.thesis.thesis_analysis.model.input;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Input {

    List<List<Span>> traces;

}
