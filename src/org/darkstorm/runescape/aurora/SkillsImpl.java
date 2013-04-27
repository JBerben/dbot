package org.darkstorm.runescape.aurora;

import org.darkstorm.runescape.api.Skills;
import org.darkstorm.runescape.api.util.Skill;

public final class SkillsImpl extends AbstractUtility implements Skills {
	private static final int[] EXPERIENCE_TABLE = { 0, 0, 83, 174, 276, 388,
			512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115,
			3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824,
			12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473,
			30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983,
			75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872,
			166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804,
			368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627,
			814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581,
			1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373,
			3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831,
			6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606,
			13034431 };

	public SkillsImpl(GameContextImpl context) {
		super(context);
	}

	@Override
	public int getExperience(Skill skill) {
		int index = skill.ordinal();
		int[] experiences = context.getClient().getSkillExperiences();
		if(experiences == null || experiences.length == 0
				|| index >= experiences.length)
			return -1;
		return experiences[index];
	}

	@Override
	public int getLevel(Skill skill) {
		int index = skill.ordinal();
		int[] levels = context.getClient().getSkillLevels();
		if(levels == null || levels.length == 0 || index >= levels.length)
			return -1;
		return levels[index];
	}

	@Override
	public int getActualLevel(Skill skill) {
		int index = skill.ordinal();
		int[] levels = context.getClient().getSkillLevelBases();
		if(levels == null || levels.length == 0 || index >= levels.length)
			return -1;
		return levels[index];
	}

	@Override
	public int getExperienceToNextLevel(Skill skill) {
		int currentLevel = getLevel(skill);
		if(currentLevel == -1)
			return -1;
		if(currentLevel == 99)
			return 0;

		int currentExperience = getExperience(skill);
		int targetExperience = EXPERIENCE_TABLE[currentLevel + 1];
		return targetExperience - currentExperience;
	}

	@Override
	public int getExperienceToLevel(Skill skill, int level) {
		int currentLevel = getLevel(skill);
		if(currentLevel == -1 || level < 0 || level > 99)
			return -1;
		if(currentLevel == 99)
			return 0;

		int currentExperience = getExperience(skill);
		int targetExperience = EXPERIENCE_TABLE[level];
		return targetExperience - currentExperience;
	}

	@Override
	public int getPercentageToNextLevel(Skill skill) {
		int currentLevel = getLevel(skill);
		if(currentLevel == -1)
			return -1;
		if(currentLevel == 99)
			return 0;

		int baseExperience = EXPERIENCE_TABLE[currentLevel];
		int currentExperience = getExperience(skill);
		int targetExperience = EXPERIENCE_TABLE[currentLevel + 1];
		return 100 * (int) ((targetExperience - currentExperience) / (double) (targetExperience - baseExperience));
	}
}
