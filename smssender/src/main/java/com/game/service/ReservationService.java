package com.game.service;

import com.game.entity.ActivityReservation;
import com.game.repository.ActivityReservationRepository;
import com.game.repository.ActivityResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2019/1/9 22:46
 */
@Service
public class ReservationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);
    private Map<String, List<String>> validCode = new HashMap<>();

    @Autowired
    private ActivityReservationRepository activityReservationRepository;

    public boolean save(List<ActivityReservation> list) {
        activityReservationRepository.saveAll(list);
        return true;
    }

    public List<String> findAll(String activityCode) {
        if(validCode.size() == 0){
            synchronized (activityReservationRepository){
                List<ActivityReservation> list = activityReservationRepository.findByStatusAndDeleted(false, false);

                for(ActivityReservation activityReservation : list){
                    if(validCode.containsKey(activityReservation.getActivityCode())){
                        List<String> activityCodeList = validCode.get(activityReservation.getActivityCode());
                        activityCodeList.add(activityReservation.getReservationCode());
                    }else{
                        List<String> activityCodeList = new ArrayList<>();
                        activityCodeList.add(activityReservation.getReservationCode());
                        validCode.put(activityReservation.getActivityCode(), activityCodeList);
                    }
                }
            }
        }
        return validCode.get(activityCode);
    }

    public void update(ActivityReservation activityReservation){
        this.activityReservationRepository.saveAndFlush(activityReservation);
    }

    public void useCode(String reservationCode, String activityCode) {
        ActivityReservation activityReservation = this.activityReservationRepository.findByReservationCodeAndActivityCodeAndStatusAndDeleted(reservationCode, activityCode,false, false);
        activityReservation.setStatus(true);
        this.activityReservationRepository.saveAndFlush(activityReservation);
    }
}
