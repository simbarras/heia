package ch.heia.isc.backend.model.participation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class    ParticipationRequest {
    private String activityId;
    private String dateOccurs;
}
