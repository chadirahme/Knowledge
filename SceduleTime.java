private boolean isValidProfileToRun(TradeConfirmNoticesProfileEntity profile)
    {
        boolean isValid=false;
        try {
            Integer scheduleHour = 0;
            Integer scheduleMinutes = 0;
            if (profile.getRunTime() == null || profile.getRunTime().isEmpty()) //user not setup the runtime in Profile
            {
                return false;
            } else {
                String[] temp = profile.getRunTime().split(":");
                if (temp.length > 1) {
                    scheduleHour = tryParse(temp[0]);
                    scheduleMinutes = tryParse(temp[1]);
                }
            }
            switch (profile.getFreqCd()) {
                case FREQUENCY_DAILY_A:
                    if (profile.getLastRunTime() == null) {
                        return checkIfValidTimeToRunProfile(scheduleHour, scheduleMinutes);
                    } else { //compare the today date with last runtime date
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date nowDate = new Date();
                        Date lastRunDate = sdf.parse(sdf.format(profile.getLastRunTime()));
                        if (lastRunDate.compareTo(sdf.parse(sdf.format(nowDate)))<0) {
                            return checkIfValidTimeToRunProfile(scheduleHour, scheduleMinutes);
                        } else { //already run
                            isValid =false;
                        }
                    }
                    break;
                default:
                    isValid = false;
                    break;
            }
        }
        catch (ParseException ex){
            throw new RuntimeException("Problem to check isValidProfileToRun Profile: "  + profile.getProfileName(),ex);
        }
        return isValid;
    }
    private Integer tryParse(Object obj) {
        Integer retVal;
        try {
            retVal = Integer.parseInt((String) obj);
        } catch (NumberFormatException nfe) {
            retVal = 0;
        }
        return retVal;
    }
    private boolean checkIfValidTimeToRunProfile(Integer scheduleHour,Integer scheduleMinutes)
    {
        try {
            Calendar now = Calendar.getInstance();
            int nowHour = now.get(Calendar.HOUR_OF_DAY);
            int nowMinute = now.get(Calendar.MINUTE);
            if(scheduleHour.compareTo(nowHour) > 0) { //scheduleHour is more than current Hour so, not valid to run at this time
                return false;
            }
            if(scheduleHour.compareTo(nowHour) < 0) { //-1: scheduleHour is less than current Hour so, no need to compare minutes in this case
                return true;
            }
            else if (scheduleHour.equals(nowHour))
            {
                if (scheduleMinutes.equals(nowMinute) || scheduleMinutes.compareTo(nowMinute) < 0) //-1: scheduleMinute is less than current Minute
                {
                    return true;
                }
            }
        }
        catch (Exception ex){
            throw new RuntimeException("Problem at check If Valid Time To Run Profile " ,ex);
        }
        return false;
    }